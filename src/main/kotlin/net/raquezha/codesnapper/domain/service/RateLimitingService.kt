package net.raquezha.codesnapper.domain.service

/**
 * RateLimitingService
 *
 * Domain service for API rate limiting and request throttling.
 * Follows Clean Architecture principles with infrastructure implementation.
 */
interface RateLimitingService {
    suspend fun checkRateLimit(clientIP: String): RateLimitResult

    suspend fun <T> withConcurrencyLimit(block: suspend () -> T): T

    sealed class RateLimitResult {
        object Allowed : RateLimitResult()

        data class RateLimited(val message: String) : RateLimitResult()
    }
}
