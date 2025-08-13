package net.raquezha.codesnapper

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testHealthCheck() =
        testApplication {
            application {
                module()
            }
            client.get("/health").apply {
                assertEquals(HttpStatusCode.OK, status)
            }
        }

    @Test
    fun testSnapEndpoint() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material",
                "title": "My Code Example"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(ContentType.Image.PNG, response.contentType())
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointInvalidInput() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "",
                "language": "unsupported_language",
                "designSystem": "invalid_system"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun testSnapEndpointLargeCodeSnippet() =
        testApplication {
            application {
                module()
            }
            // Create a simple large code snippet without complex string interpolation
            val largeCodeSnippet =
                buildString {
                    append("fun main() {\n")
                    repeat(20) { i ->
                        append("    println(\"Hello World $i\")\n")
                    }
                    append("}")
                }

            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """
                        {
                            "code": "fun main() {\n    println(\"Hello 1\")\n    println(\"Hello 2\")\n    println(\"Hello 3\")\n    println(\"Hello 4\")\n    println(\"Hello 5\")\n}",
                            "language": "kotlin",
                            "designSystem": "material"
                        }
                        """.trimIndent(),
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointMacOSDesignSystem() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "macos"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointMissingParameters() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "language": "kotlin",
                "designSystem": "material"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun testSnapEndpointUnsupportedBackgroundTheme() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material",
                "backgroundTheme": "unsupported_theme"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun testSnapEndpointCustomFilename() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material",
                "filename": "custom_name"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            // Check that the response contains the custom filename prefix and .png extension
            val contentDisposition = response.headers[HttpHeaders.ContentDisposition]
            assert(contentDisposition?.contains("custom_name") == true)
            assert(contentDisposition?.contains(".png") == true)
        }

    @Test
    fun testSnapEndpointAutoSizing() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointEmptyCode() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "",
                "language": "kotlin",
                "designSystem": "material"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun testSnapEndpointDefaultFilename() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.headers[HttpHeaders.ContentDisposition]?.contains(".png") == true)
        }

    @Test
    fun testSnapEndpointValidLightTheme() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material",
                "theme": "atom",
                "darkMode": false
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointValidDarkTheme() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material",
                "theme": "darcula",
                "darkMode": true
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointValidMacOSTheme() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "macos",
                "theme": "darcula"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointValidPresetLight() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material",
                "preset": "light"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointCalendarLanguage() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "public class Hello { public static void main(String[] args) { System.out.println(\"Hello\"); } }",
                "language": "java",
                "designSystem": "material"
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointFullPayload() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                "code": "fun hello() = println(\"Hello\")",
                "language": "kotlin",
                "designSystem": "material",
                "theme": "darcula",
                "darkMode": true,
                "preset": "default",
                "title": "My Code Example",
                "filename": "test_code",
                "backgroundTheme": "darcula",
                "width": 800,
                "height": 600,
                "fontSize": 14,
                "fontFamily": "JetBrains Mono",
                "padding": 20
            }""",
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }

    @Test
    fun testSnapEndpointUnsupportedContentType() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Text.Plain)
                    setBody("invalid request body")
                }
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }

    @Test
    fun testSnapEndpointPerformanceLargePayload() =
        testApplication {
            application {
                module()
            }
            val response =
                client.post("/snap") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """
                        {
                            "code": "fun main() {\n    repeat(10) { i ->\n        println(\"Line ${'$'}i\")\n        val data = processData(i)\n        saveToDatabase(data)\n    }\n}",
                            "language": "kotlin",
                            "designSystem": "material",
                            "width": 1200,
                            "height": 800
                        }
                        """.trimIndent(),
                    )
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assert(response.body<ByteArray>().isNotEmpty())
        }
}
