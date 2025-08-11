package net.raquezha.codesnapper.domain.service

import net.raquezha.codesnapper.domain.model.CodeSnippet
import net.raquezha.codesnapper.domain.model.HighlightedCode

interface CodeHighlighterService {
    fun highlight(snippet: CodeSnippet): HighlightedCode
}
