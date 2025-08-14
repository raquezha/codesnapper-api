package net.raquezha.codesnapper

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PresetTests {
    private val outerMargin = 100
    private val scaleFactor = 2
    private val titleBarHeight = 44

    private fun pngDimensions(bytes: ByteArray): Pair<Int, Int> {
        require(
            bytes.size > 24 &&
                bytes.copyOfRange(
                    0,
                    8,
                ).contentEquals(
                    byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10),
                ),
        ) { "Not a PNG" }
        val w =
            ((bytes[16].toInt() and 0xFF) shl 24) or
                ((bytes[17].toInt() and 0xFF) shl 16) or
                ((bytes[18].toInt() and 0xFF) shl 8) or
                (bytes[19].toInt() and 0xFF)
        val h =
            ((bytes[20].toInt() and 0xFF) shl 24) or
                ((bytes[21].toInt() and 0xFF) shl 16) or
                ((bytes[22].toInt() and 0xFF) shl 8) or
                (bytes[23].toInt() and 0xFF)
        return w to h
    }

    private suspend fun assertPngResponse(response: HttpResponse): ByteArray {
        if (response.status != HttpStatusCode.OK) {
            val text =
                try {
                    response.bodyAsText()
                } catch (e: Exception) {
                    "<unreadable body: ${e.message}>"
                }
            throw AssertionError("Expected 200 OK, got ${response.status}. Body: $text")
        }
        val ct = response.contentType()
        assertTrue(ct?.match(ContentType.Image.PNG) == true, "Expected PNG content type, was $ct")
        return response.body()
    }

    @Test
    fun `should produce valid image for presentation preset`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"fun main() { println(\"Hi\") }",
                    |"language":"kotlin",
                    |"preset":"presentation"
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val bytes = assertPngResponse(response)
            val (w, h) = pngDimensions(bytes)
            // Verify reasonable dimensions for presentation preset (should be larger)
            assertTrue(w > 2000, "Presentation preset should produce wide images, got width: $w")
            assertTrue(h > 1000, "Presentation preset should produce tall images, got height: $h")
            assertTrue(bytes.isNotEmpty(), "Image should contain data")
        }

    @Test
    fun `should produce valid image for compact preset`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(\"x\")",
                    |"language":"kotlin",
                    |"preset":"compact"
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val bytes = assertPngResponse(response)
            val (w, h) = pngDimensions(bytes)
            // Verify reasonable dimensions for compact preset
            assertTrue(w > 500, "Compact preset should produce valid width, got: $w")
            assertTrue(h > 500, "Compact preset should produce valid height, got: $h")
            assertTrue(bytes.isNotEmpty(), "Image should contain data")
        }

    @Test
    fun `should override width and height when preset is specified`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(\"override\")",
                    |"language":"kotlin",
                    |"preset":"compact",
                    |"width":1000,
                    |"height":700
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val (w, h) = pngDimensions(assertPngResponse(response))
            val expectedFinalWidth = (1000 + outerMargin * 2) * scaleFactor
            val expectedFinalHeight = ((700 + titleBarHeight) + outerMargin * 2) * scaleFactor
            assertEquals(expectedFinalWidth, w)
            assertEquals(expectedFinalHeight, h)
        }

    @Test
    fun `should reject invalid preset with proper error message`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(1)",
                    |"language":"kotlin",
                    |"preset":"gigantic"
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)
            val bodyText = response.bodyAsText()
            assertTrue(
                bodyText.contains("UNSUPPORTED_PRESET"),
                "Missing UNSUPPORTED_PRESET in: $bodyText",
            )
        }

    @Test
    fun `should allow font size override on presentation preset`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(\"size\")",
                    |"language":"kotlin",
                    |"preset":"presentation",
                    |"fontSize":16
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val bytes = assertPngResponse(response)
            assertTrue(bytes.isNotEmpty())
        }

    @Test
    fun `should produce same dimensions for default preset and no preset`() =
        testApplication {
            application { module() }
            val respNoPreset =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(\"a\")",
                    |"language":"kotlin"
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val respDefault =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(\"a\")",
                    |"language":"kotlin",
                    |"preset":"default"
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val (w1, h1) = pngDimensions(assertPngResponse(respNoPreset))
            val (w2, h2) = pngDimensions(assertPngResponse(respDefault))
            assertEquals(w1, w2)
            assertEquals(h1, h2)
        }

    @Test
    fun `should produce valid image for light preset`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(\"light\")",
                    |"language":"kotlin",
                    |"preset":"light"
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val bytes = assertPngResponse(response)
            val (w, h) = pngDimensions(bytes)
            assertTrue(w > 0 && h > 0, "Light preset should produce valid dimensions")
            assertTrue(bytes.isNotEmpty(), "Image should contain data")
        }

    @Test
    fun `should produce valid image for default preset`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"fun main() { println(\"Default\") }",
                    |"language":"kotlin",
                    |"preset":"default"
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val bytes = assertPngResponse(response)
            val (w, h) = pngDimensions(bytes)
            // Verify reasonable dimensions for default preset (adjusted threshold)
            assertTrue(w > 1000, "Default preset should produce reasonable width, got: $w")
            assertTrue(h > 700, "Default preset should produce reasonable height, got: $h")
            assertTrue(bytes.isNotEmpty(), "Image should contain data")
        }

    @Test
    fun `should allow overrides with light preset`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"fun main() { println(\"Override\") }",
                    |"language":"kotlin",
                    |"preset":"light",
                    |"width":1200,
                    |"height":600
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val (w, h) = pngDimensions(assertPngResponse(response))
            val expectedFinalWidth = (1200 + outerMargin * 2) * scaleFactor
            val expectedFinalHeight = ((600 + titleBarHeight) + outerMargin * 2) * scaleFactor
            assertEquals(expectedFinalWidth, w)
            assertEquals(expectedFinalHeight, h)
        }

    @Test
    fun `should produce valid image with different design system and theme`() =
        testApplication {
            application { module() }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"fun main() { println(\"Combo\") }",
                    |"language":"kotlin",
                    |"preset":"presentation",
                    |"designSystem":"macos",
                    |"backgroundTheme":"darcula"
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            val bytes = assertPngResponse(response)
            val (w, h) = pngDimensions(bytes)
            // Verify the combination works and produces reasonable output
            assertTrue(w > 2000, "Presentation with design system should produce wide images, got: $w")
            assertTrue(h > 1000, "Presentation with design system should produce tall images, got: $h")
            assertTrue(bytes.isNotEmpty(), "Image should contain data")
        }

    @Test
    fun `should produce different sizes for different presets`() =
        testApplication {
            application { module() }

            val compactResponse =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                    |"code":"println(\"test\")",
                    |"language":"kotlin",
                    |"preset":"compact"
                    |}
                        """.trimMargin(),
                    )
                }

            val presentationResponse =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                    |"code":"println(\"test\")",
                    |"language":"kotlin",
                    |"preset":"presentation"
                    |}
                        """.trimMargin(),
                    )
                }

            val (compactW, compactH) = pngDimensions(assertPngResponse(compactResponse))
            val (presentationW, presentationH) = pngDimensions(assertPngResponse(presentationResponse))

            // Presentation preset should be larger than compact
            assertTrue(
                presentationW > compactW,
                "Presentation width ($presentationW) should be larger than compact width ($compactW)",
            )
            assertTrue(
                presentationH > compactH,
                "Presentation height ($presentationH) should be larger than compact height ($compactH)",
            )
        }

    @Test
    fun `should handle min and max edge cases for presets`() =
        testApplication {
            application { module() }
            // Test with larger minimum dimensions that should be accepted
            val minResponse =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(\"min\")",
                    |"language":"kotlin",
                    |"preset":"compact",
                    |"width":400,
                    |"height":300
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            assertEquals(HttpStatusCode.OK, minResponse.status)
            val minBytes = assertPngResponse(minResponse)
            assertTrue(minBytes.isNotEmpty(), "Minimum size image should contain data")

            // Test with large dimensions that should be accepted
            val maxResponse =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    val body =
                        """{
                    |"code":"println(\"max\")",
                    |"language":"kotlin",
                    |"preset":"presentation",
                    |"width":2000,
                    |"height":1500
                    |}
                        """.trimMargin()
                    setBody(body)
                }
            assertEquals(HttpStatusCode.OK, maxResponse.status)
            val maxBytes = assertPngResponse(maxResponse)
            assertTrue(maxBytes.isNotEmpty(), "Large size image should contain data")
        }
}
