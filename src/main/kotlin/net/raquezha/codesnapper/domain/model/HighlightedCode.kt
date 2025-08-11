package net.raquezha.codesnapper.domain.model

import dev.snipme.highlights.model.CodeHighlight

data class HighlightedCode(
    val code: String,
    val highlights: List<CodeHighlight>
)
