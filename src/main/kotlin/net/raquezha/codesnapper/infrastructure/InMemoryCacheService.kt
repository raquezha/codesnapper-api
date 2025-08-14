package net.raquezha.codesnapper.infrastructure

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.raquezha.codesnapper.domain.service.CacheService
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * InMemoryCacheService
 *
 * Infrastructure implementation of CacheService using in-memory storage.
 * Follows established Clean Architecture pattern: domain interface → infrastructure implementation.
 */
class InMemoryCacheService : CacheService {
    companion object {
        private val logger = LoggerFactory.getLogger(InMemoryCacheService::class.java)
        private const val MAX_CACHE_SIZE = 1000
        private const val CACHE_TTL_HOURS = 24L
        private const val CLEANUP_INTERVAL_MINUTES = 30L
    }

    private val cache = ConcurrentHashMap<String, CacheEntry>()
    private val accessOrder = mutableListOf<String>()
    private val accessMutex = Mutex()
    private var lastCleanup = Instant.now()

    data class CacheEntry(
        val imageBytes: ByteArray,
        val createdAt: Instant,
        var lastAccessed: Instant,
        val metadata: CacheService.CacheMetadata,
    ) {
        fun isExpired(): Boolean = Duration.between(createdAt, Instant.now()).toHours() > CACHE_TTL_HOURS

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as CacheEntry
            return imageBytes.contentEquals(other.imageBytes)
        }

        override fun hashCode(): Int = imageBytes.contentHashCode()
    }

    override fun generateCacheKey(
        code: String,
        language: String,
        theme: String?,
        preset: String?,
        width: Int?,
        height: Int?,
        fontSize: Int?,
        designSystem: String?,
    ): String {
        val keyData = "$code|$language|$theme|$preset|$width|$height|$fontSize|$designSystem"
        return MessageDigest.getInstance("SHA-256")
            .digest(keyData.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    override suspend fun getCachedImage(cacheKey: String): ByteArray? {
        val entry = cache[cacheKey] ?: return null

        if (entry.isExpired()) {
            cache.remove(cacheKey)
            logger.debug("Cache entry expired and removed: {}", cacheKey)
            return null
        }

        // Update access tracking
        accessMutex.withLock {
            entry.lastAccessed = Instant.now()
            accessOrder.remove(cacheKey)
            accessOrder.add(cacheKey)
        }

        logger.debug("Cache hit for key: {}", cacheKey)
        return entry.imageBytes
    }

    override suspend fun cacheImage(
        cacheKey: String,
        imageBytes: ByteArray,
        metadata: CacheService.CacheMetadata,
    ) {
        performMaintenanceIfNeeded()

        val entry =
            CacheEntry(
                imageBytes = imageBytes,
                createdAt = Instant.now(),
                lastAccessed = Instant.now(),
                metadata = metadata,
            )

        cache[cacheKey] = entry

        accessMutex.withLock {
            accessOrder.add(cacheKey)

            // Evict oldest entries if cache is full
            while (cache.size > MAX_CACHE_SIZE) {
                val oldestKey = accessOrder.removeFirstOrNull()
                if (oldestKey != null) {
                    cache.remove(oldestKey)
                    logger.debug("Evicted cache entry: {}", oldestKey)
                }
            }
        }

        logger.debug("Cached image: key={} size={}bytes", cacheKey, imageBytes.size)
    }

    override fun getStats(): Map<String, Any> {
        val now = Instant.now()
        val expiredCount = cache.values.count { it.isExpired() }
        val totalSize = cache.values.sumOf { it.imageBytes.size }

        return mapOf(
            "total_entries" to cache.size,
            "expired_entries" to expiredCount,
            "total_size_bytes" to totalSize,
            "total_size_mb" to String.format("%.2f", totalSize / 1024.0 / 1024.0),
            "oldest_entry_age_hours" to
                (
                    cache.values.minOfOrNull {
                        Duration.between(it.createdAt, now).toHours()
                    } ?: 0L
                ).toInt(),
            // Convert Long to Int for serialization
            "cache_hit_potential" to
                if (cache.isNotEmpty()) {
                    String.format("%.1f%%", (cache.size - expiredCount).toDouble() / cache.size * 100)
                } else {
                    "0.0%"
                },
        )
    }

    private suspend fun performMaintenanceIfNeeded() {
        val now = Instant.now()
        if (Duration.between(lastCleanup, now).toMinutes() < CLEANUP_INTERVAL_MINUTES) return

        lastCleanup = now
        var removedCount = 0

        // Collect expired keys first without using removeIf with suspension
        val expiredKeys =
            cache.entries.mapNotNull { (key, entry) ->
                if (entry.isExpired()) key else null
            }

        // Remove expired entries and update access order
        for (key in expiredKeys) {
            cache.remove(key)
            accessMutex.withLock {
                accessOrder.remove(key)
            }
            removedCount++
        }

        if (removedCount > 0) {
            logger.info("Cache maintenance: removed {} expired entries", removedCount)
        }
    }
}
