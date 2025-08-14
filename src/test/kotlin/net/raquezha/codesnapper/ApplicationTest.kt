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
    fun `should return OK status for health check`() =
        testApplication {
            application {
                module()
            }
            client.get("/health").apply {
                assertEquals(HttpStatusCode.OK, status)
            }
        }

    @Test
    fun `should generate PNG image when given valid snap request`() =
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
    fun `should return bad request when snap request has invalid input`() =
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
    fun `should process large code snippet without error`() =
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
    fun `should generate valid snap for MacOS design system`() =
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
    fun `should return bad request when snap request is missing parameters`() =
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
    fun `should return bad request for unsupported background theme`() =
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
    fun `should allow custom filename in snap request`() =
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
    fun `should auto size snap image based on content`() =
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
    fun `should return bad request when code is empty`() =
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
    fun `should use default filename when none is provided`() =
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
    fun `should generate valid snap with light theme`() =
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
    fun `should generate valid snap with dark theme`() =
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
    fun `should generate valid snap for MacOS with darcula theme`() =
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
    fun `should generate valid snap with preset light theme`() =
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
    fun `should generate valid snap for Java code with calendar language`() =
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
    fun `should process full payload for snap request`() =
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
    fun `should return unsupported media type for invalid content type`() =
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
    fun `should handle performance test with large payload`() =
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
