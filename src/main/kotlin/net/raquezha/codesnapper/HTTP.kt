package net.raquezha.codesnapper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowHeader("X-Requested-With")

        // 🔒 SECURITY FIX: Replace anyHost() with specific allowed origins
        allowHost("localhost:3000") // Local development
        allowHost("localhost:8080") // Local testing
        // Add your production domains here
        // allowHost("your-frontend-domain.com", schemes = listOf("https"))

        allowCredentials = false // Disable credentials for security
        maxAgeInSeconds = 86400 // 24 hours cache
    }
}
