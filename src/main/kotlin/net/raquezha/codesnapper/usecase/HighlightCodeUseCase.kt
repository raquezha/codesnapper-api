package net.raquezha.codesnapper.usecase

import net.raquezha.codesnapper.infrastructure.HighlightsCodeHighlighterService
import net.raquezha.codesnapper.domain.model.CodeSnippet
import net.raquezha.codesnapper.domain.model.HighlightedCode

class HighlightCodeUseCase(private val highlighterService: HighlightsCodeHighlighterService) {
    fun execute(snippet: CodeSnippet): HighlightedCode {
        return highlighterService.highlight(snippet)
    }
}
