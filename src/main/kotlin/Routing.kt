package net.raquezha

import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.CodeHighlight
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
            val html = highlightsToHtml(snapRequest.code, highlighted)
            call.respondText(html, ContentType.Text.Html)
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

fun highlightsToHtml(code: String, highlights: List<CodeHighlight>): String {
    val sb = StringBuilder()
    // Sort highlights by start position
    val sorted = highlights.sortedBy { it.location.start }
    var last = 0
    for (highlight in sorted) {
        val start = highlight.location.start
        val end = highlight.location.end
        if (start > last) {
            sb.append(escapeHtml(code.substring(last, start)))
        }
        val color = when (highlight) {
            is dev.snipme.highlights.model.ColorHighlight -> String.format("#%06X", 0xFFFFFF and highlight.rgb)
            else -> null
        }
        if (color != null) {
            sb.append("<span style=\"color: $color\">")
        } else if (highlight is dev.snipme.highlights.model.BoldHighlight) {
            sb.append("<b>")
        }
        sb.append(escapeHtml(code.substring(start, end)))
        if (color != null) {
            sb.append("</span>")
        } else if (highlight is dev.snipme.highlights.model.BoldHighlight) {
            sb.append("</b>")
        }
        last = end
    }
    if (last < code.length) {
        sb.append(escapeHtml(code.substring(last)))
    }
    return "<pre style=\"margin:0;padding:0;font-family:monospace;font-size:1em;\">$sb</pre>"
}

fun escapeHtml(text: String): String = text
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")
