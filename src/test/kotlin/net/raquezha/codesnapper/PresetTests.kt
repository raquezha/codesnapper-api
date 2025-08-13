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
    fun presentationPresetProducesExpectedDimensions() =
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
            val (w, h) = pngDimensions(assertPngResponse(response))
            val expectedFinalWidth = (1920 + outerMargin * 2) * scaleFactor
            val expectedFinalHeight = ((1080 + titleBarHeight) + outerMargin * 2) * scaleFactor
            assertEquals(expectedFinalWidth, w)
            assertEquals(expectedFinalHeight, h)
        }

    @Test
    fun compactPresetProducesExpectedDimensions() =
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
            val (w, h) = pngDimensions(assertPngResponse(response))
            val expectedFinalWidth = (800 + outerMargin * 2) * scaleFactor
            val expectedFinalHeight = ((600 + titleBarHeight) + outerMargin * 2) * scaleFactor
            assertEquals(expectedFinalWidth, w)
            assertEquals(expectedFinalHeight, h)
        }

    @Test
    fun overrideWidthHeightWithPreset() =
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
    fun invalidPresetRejected() =
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
    fun fontSizeOverrideOnPresentation() =
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
    fun defaultPresetSameAsNoPresetDimensions() =
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
    fun lightPresetProducesImage() =
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
            val (w, h) = pngDimensions(assertPngResponse(response))
            assertTrue(w > 0 && h > 0)
        }
}
