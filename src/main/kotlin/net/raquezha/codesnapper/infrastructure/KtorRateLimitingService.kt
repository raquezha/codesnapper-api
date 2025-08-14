package net.raquezha.codesnapper.infrastructure

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import net.raquezha.codesnapper.domain.service.RateLimitingService
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * KtorRateLimitingService
 *
 * Infrastructure implementation of RateLimitingService using Ktor-specific features.
 * Follows established Clean Architecture pattern: domain interface → infrastructure implementation.
 */
class KtorRateLimitingService : RateLimitingService {
    companion object {
        private const val MAX_REQUESTS_PER_MINUTE = 30
        private const val MAX_REQUESTS_PER_HOUR = 300
        private const val MAX_CONCURRENT_REQUESTS = 10
        private const val CLEANUP_INTERVAL_MINUTES = 5
    }

    private val requestCounts = ConcurrentHashMap<String, RateLimitBucket>()
    private val concurrentRequests = Semaphore(MAX_CONCURRENT_REQUESTS)
    private var lastCleanup = Instant.now()

    data class RateLimitBucket(
        val minuteCount: AtomicInteger = AtomicInteger(0),
        val hourCount: AtomicInteger = AtomicInteger(0),
        var lastMinuteReset: Instant = Instant.now(),
        var lastHourReset: Instant = Instant.now(),
    )

    override suspend fun checkRateLimit(clientIP: String): RateLimitingService.RateLimitResult {
        cleanupOldEntries()

        val bucket = requestCounts.computeIfAbsent(clientIP) { RateLimitBucket() }
        val now = Instant.now()

        // Reset counters if time windows have passed
        if (Duration.between(bucket.lastMinuteReset, now).toMinutes() >= 1) {
            bucket.minuteCount.set(0)
            bucket.lastMinuteReset = now
        }

        if (Duration.between(bucket.lastHourReset, now).toHours() >= 1) {
            bucket.hourCount.set(0)
            bucket.lastHourReset = now
        }

        val minuteCount = bucket.minuteCount.incrementAndGet()
        val hourCount = bucket.hourCount.incrementAndGet()

        return when {
            minuteCount > MAX_REQUESTS_PER_MINUTE ->
                RateLimitingService.RateLimitResult.RateLimited(
                    "Too many requests per minute. Limit: $MAX_REQUESTS_PER_MINUTE",
                )
            hourCount > MAX_REQUESTS_PER_HOUR ->
                RateLimitingService.RateLimitResult.RateLimited(
                    "Too many requests per hour. Limit: $MAX_REQUESTS_PER_HOUR",
                )
            else -> RateLimitingService.RateLimitResult.Allowed
        }
    }

    override suspend fun <T> withConcurrencyLimit(block: suspend () -> T): T {
        return concurrentRequests.withPermit { block() }
    }

    private fun cleanupOldEntries() {
        val now = Instant.now()
        if (Duration.between(lastCleanup, now).toMinutes() < CLEANUP_INTERVAL_MINUTES) return

        lastCleanup = now
        val cutoff = now.minus(Duration.ofHours(2))

        requestCounts.entries.removeIf { (_, bucket) ->
            bucket.lastHourReset.isBefore(cutoff)
        }
    }
}

/**
 * Rate limiting plugin for Ktor
 */
fun Application.installRateLimiting() {
    val rateLimitingService = KtorRateLimitingService()

    intercept(ApplicationCallPipeline.Plugins) {
        // Fixed: get client IP using the correct Ktor API
        val clientIP =
            call.request.headers["X-Forwarded-For"]?.split(",")?.first()?.trim()
                ?: call.request.local.remoteHost
                ?: "unknown"

        when (val result = rateLimitingService.checkRateLimit(clientIP)) {
            is RateLimitingService.RateLimitResult.RateLimited -> {
                call.respond(
                    HttpStatusCode.TooManyRequests,
                    mapOf(
                        "error" to "Rate Limit Exceeded",
                        "message" to result.message,
                        "code" to "RATE_LIMIT_EXCEEDED",
                    ),
                )
                return@intercept finish()
            }
            RateLimitingService.RateLimitResult.Allowed -> {
                // Continue with request processing
            }
        }

        // Apply concurrency limits for resource-intensive endpoints
        if (call.request.uri.contains("/snap")) {
            rateLimitingService.withConcurrencyLimit {
                proceed()
            }
        } else {
            proceed()
        }
    }
}
