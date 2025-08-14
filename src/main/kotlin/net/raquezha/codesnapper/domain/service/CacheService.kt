package net.raquezha.codesnapper.domain.service

/**
 * CacheService
 *
 * Domain service for image caching and retrieval.
 * Follows Clean Architecture principles with infrastructure implementation.
 */
interface CacheService {
    suspend fun getCachedImage(cacheKey: String): ByteArray?

    suspend fun cacheImage(
        cacheKey: String,
        imageBytes: ByteArray,
        metadata: CacheMetadata,
    )

    fun generateCacheKey(
        code: String,
        language: String,
        theme: String?,
        preset: String?,
        width: Int?,
        height: Int?,
        fontSize: Int?,
        designSystem: String?,
    ): String

    fun getStats(): Map<String, Any>

    data class CacheMetadata(
        val codeLength: Int,
        val language: String,
        val preset: String?,
        val dimensions: Pair<Int, Int>,
    )
}
