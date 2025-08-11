package net.raquezha.codesnapper.usecase

import net.raquezha.codesnapper.domain.model.CodeSnippet
import net.raquezha.codesnapper.domain.model.ImageConfiguration
import net.raquezha.codesnapper.domain.service.CodeHighlighterService
import net.raquezha.codesnapper.domain.service.ImageRenderingService

class GenerateCodeImageUseCase(
    private val highlighterService: CodeHighlighterService,
    private val imageRenderingService: ImageRenderingService,
) {
    fun execute(
        snippet: CodeSnippet,
        config: ImageConfiguration = ImageConfiguration(),
    ): ByteArray {
        val highlightedCode = highlighterService.highlight(snippet)
        return imageRenderingService.renderToPng(highlightedCode, config)
    }
}
