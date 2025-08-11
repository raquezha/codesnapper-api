package net.raquezha.codesnapper

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
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
}
