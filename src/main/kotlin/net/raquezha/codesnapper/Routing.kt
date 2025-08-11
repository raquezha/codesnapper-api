package net.raquezha.codesnapper

import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.CodeHighlight
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.highlights.model.SyntaxThemes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.raquezha.codesnapper.controller.SnapRequest
import net.raquezha.codesnapper.domain.model.CodeSnippet
import net.raquezha.codesnapper.usecase.GenerateCodeImageUseCase
import org.koin.ktor.ext.inject
import java.util.Locale

private const val RGB_MASK = 0xFFFFFF

fun Application.configureRouting() {
    val generateCodeImageUseCase by inject<GenerateCodeImageUseCase>()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        post("/snap") {
            val snapRequest = call.receive<SnapRequest>()
            if (!isSupportedLanguage(snapRequest.language)) {
                call.respond(HttpStatusCode.BadRequest, "Unsupported language: ${snapRequest.language}")
                return@post
            }
            if (!isSupportedTheme(snapRequest.theme)) {
                call.respond(HttpStatusCode.BadRequest, "Unsupported theme: ${snapRequest.theme}")
                return@post
            }
            val snippet =
                CodeSnippet(
                    code = snapRequest.code,
                    language = snapRequest.language,
                    theme = snapRequest.theme,
                    darkMode = snapRequest.darkMode,
                )

            // Generate PNG image
            val imageBytes = generateCodeImageUseCase.execute(snippet)

            // Return PNG image with correct content type
            call.respondBytes(imageBytes, ContentType.Image.PNG)
        }
    }
}

fun parseSyntaxLanguage(name: String?): SyntaxLanguage {
    return SyntaxLanguage.entries.firstOrNull {
        it.name.equals(name, ignoreCase = true)
    } ?: SyntaxLanguage.KOTLIN
}

fun parseSyntaxTheme(
    name: String?,
    darkMode: Boolean = true,
): SyntaxTheme {
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

fun highlightsToHtml(
    code: String,
    highlights: List<CodeHighlight>,
): String {
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
        val color =
            when (highlight) {
                is ColorHighlight -> String.format(Locale.US, "#%06X", RGB_MASK and highlight.rgb)
                else -> null
            }
        if (color != null) {
            sb.append("<span style=\"color: $color\">")
        } else if (highlight is BoldHighlight) {
            sb.append("<b>")
        }
        sb.append(escapeHtml(code.substring(start, end)))
        if (color != null) {
            sb.append("</span>")
        } else if (highlight is BoldHighlight) {
            sb.append("</b>")
        }
        last = end
    }
    if (last < code.length) {
        sb.append(escapeHtml(code.substring(last)))
    }
    return "<pre style=\"margin:0;padding:0;font-family:monospace;font-size:1em;\">$sb</pre>"
}

fun escapeHtml(text: String): String =
    text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

fun isSupportedLanguage(name: String?): Boolean {
    return SyntaxLanguage.entries.any { it.name.equals(name, ignoreCase = true) }
}

fun isSupportedTheme(name: String?): Boolean {
    return when (name?.lowercase()) {
        "darcula", "monokai", "notepad", "matrix", "pastel", "atom", "atomone", "atom_one" -> true
        else -> false
    }
}
