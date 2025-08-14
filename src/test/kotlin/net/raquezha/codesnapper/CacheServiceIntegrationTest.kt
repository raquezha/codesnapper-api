package net.raquezha.codesnapper

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import net.raquezha.codesnapper.domain.service.CacheService
import net.raquezha.codesnapper.infrastructure.InMemoryCacheService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CacheServiceIntegrationTest {
    @Test
    fun `should generate cache keys correctly`() {
        // Test the cache service directly without DI complications
        val cacheService: CacheService = InMemoryCacheService()

        val cacheKey =
            cacheService.generateCacheKey(
                code = "fun main() { println(\"test\") }",
                language = "kotlin",
                theme = "darcula",
                preset = "default",
                width = 800,
                height = 600,
                fontSize = 14,
                designSystem = "macos",
            )

        assertTrue(cacheKey.isNotEmpty(), "Cache key should not be empty")
        assertEquals(64, cacheKey.length, "SHA-256 cache key should be 64 characters")
        println("✅ Cache key generated: ${cacheKey.take(16)}...")
    }

    @Test
    fun `should cache and retrieve images correctly`() =
        testApplication {
            application { module() }

            // Test the cache service directly to avoid DI issues
            val cacheService: CacheService = InMemoryCacheService()

            // Create test data
            val testImageBytes = "fake-png-data".toByteArray()
            val metadata =
                CacheService.CacheMetadata(
                    codeLength = 100,
                    language = "kotlin",
                    preset = "default",
                    dimensions = 800 to 600,
                )

            val cacheKey =
                cacheService.generateCacheKey(
                    code = "test code",
                    language = "kotlin",
                    theme = "darcula",
                    preset = "default",
                    width = 800,
                    height = 600,
                    fontSize = 14,
                    designSystem = "macos",
                )

            // Test cache miss
            val cachedBeforeStore = cacheService.getCachedImage(cacheKey)
            assertTrue(cachedBeforeStore == null, "Should be cache miss initially")

            // Cache the image
            cacheService.cacheImage(cacheKey, testImageBytes, metadata)

            // Test cache hit
            val cachedAfterStore = cacheService.getCachedImage(cacheKey)
            assertNotNull(cachedAfterStore, "Should be cache hit after storing")
            assertTrue(cachedAfterStore.contentEquals(testImageBytes), "Cached data should match original")

            println("✅ Cache store and retrieve working correctly")
        }

    @Test
    fun `should provide cache stats`() {
        // Test the cache service directly
        val cacheService: CacheService = InMemoryCacheService()

        // Get initial stats
        val stats = cacheService.getStats()

        // Verify stats structure
        assertTrue(stats.containsKey("total_entries"), "Stats should contain total_entries")
        assertTrue(stats.containsKey("total_size_bytes"), "Stats should contain total_size_bytes")
        assertTrue(stats.containsKey("total_size_mb"), "Stats should contain total_size_mb")
        assertTrue(stats.containsKey("cache_hit_potential"), "Stats should contain cache_hit_potential")

        println("✅ Cache stats: $stats")
    }

    @Test
    fun `should handle API request with cache service integrated`() =
        testApplication {
            application { module() }

            // Test that the API works with the new cache service in the DI container
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun main() { println(\"Cache integration test!\") }",
                "language": "kotlin",
                "preset": "compact"
            }""",
                    )
                }

            assertEquals(HttpStatusCode.OK, response.status)
            println("✅ API request successful with cache service integrated")
        }
}
