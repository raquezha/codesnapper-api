package net.raquezha

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.event.*

@Serializable
data class SnapRequest(val code: String, val language: String, val theme: String)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        post("/snap") {
            val snapRequest = call.receive<SnapRequest>()
            call.respondText("Received code: ${snapRequest.code}, language: ${snapRequest.language}, theme: ${snapRequest.theme}", ContentType.Text.Plain)
        }
    }
}
