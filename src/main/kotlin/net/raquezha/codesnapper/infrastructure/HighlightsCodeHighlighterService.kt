package net.raquezha.codesnapper.infrastructure

import dev.snipme.highlights.Highlights
import net.raquezha.codesnapper.domain.model.CodeSnippet
import net.raquezha.codesnapper.domain.model.HighlightedCode
import net.raquezha.codesnapper.domain.service.CodeHighlighterService
import net.raquezha.codesnapper.util.parseSyntaxLanguage
import net.raquezha.codesnapper.util.parseSyntaxTheme

/**
 * HighlightsCodeHighlighterService
 *
 * This class is an implementation of the [CodeHighlighterService] interface as part of the infrastructure layer
 * in a clean architecture setup. It is responsible for providing code highlighting functionality by integrating
 * with the Highlights syntax highlighting library. This separation allows the domain layer to remain independent
 * of external libraries and frameworks.
 *
 * The service uses the Highlights library to parse and highlight code according to the specified language and theme,
 * returning a [HighlightedCode] object containing the original code and a list of syntax highlight tokens.
 */
class HighlightsCodeHighlighterService : CodeHighlighterService {
    override fun highlight(snippet: CodeSnippet): HighlightedCode {
        val language = parseSyntaxLanguage(snippet.language)
        val theme = parseSyntaxTheme(snippet.theme, snippet.darkMode)

        val highlights =
            Highlights.Builder()
                .theme(theme)
                .language(language)
                .code(snippet.code)
                .build()

        return HighlightedCode(
            code = snippet.code,
            highlights = highlights.getHighlights(),
        )
    }
}
