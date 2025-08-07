package net.raquezha

import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.highlights.model.SyntaxThemes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class SnapRequest(val code: String, val language: String, val theme: String, val darkMode: Boolean = true)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        post("/snap") {
            val snapRequest = call.receive<SnapRequest>()
            val language = parseSyntaxLanguage(snapRequest.language)
            val theme = parseSyntaxTheme(snapRequest.theme, snapRequest.darkMode)
            val highlights = Highlights.Builder()
                .code(snapRequest.code)
                .language(language)
                .theme(theme)
                .build()
            val highlighted = highlights.getHighlights()
            // For now, just return the highlights as a string for testing
            call.respondText(highlighted.joinToString("\n"), ContentType.Text.Plain)
        }
    }
}

fun parseSyntaxLanguage(name: String?): SyntaxLanguage {
    return SyntaxLanguage.entries.firstOrNull {
        it.name.equals(name, ignoreCase = true)
    } ?: SyntaxLanguage.KOTLIN
}

fun parseSyntaxTheme(name: String?, darkMode: Boolean = true): SyntaxTheme {
    return when (name?.lowercase()) {
        "darcula" -> SyntaxThemes.darcula(darkMode)
        "monokai" -> SyntaxThemes.monokai(darkMode)
        "notepad" -> SyntaxThemes.notepad(darkMode)
        "matrix" -> SyntaxThemes.matrix(darkMode)
        "pastel" -> SyntaxThemes.pastel(darkMode)
        "atom", "atomone", "atom_one" -> SyntaxThemes.atom(darkMode)
        else -> SyntaxThemes.darcula(darkMode)
    }
}
