package net.raquezha.codesnapper.usecase

import net.raquezha.codesnapper.domain.model.CodeSnippet
import net.raquezha.codesnapper.domain.model.ImageConfiguration
import net.raquezha.codesnapper.domain.service.CodeHighlighterService
import net.raquezha.codesnapper.domain.service.ImageRenderingService

/**
 * GenerateCodeImageUseCase
 *
 * Orchestrates the complete process of converting code input into a PNG image:
 * 1. Takes a CodeSnippet and ImageConfiguration
 * 2. Applies syntax highlighting via CodeHighlighterService
 * 3. Renders the highlighted code as PNG via ImageRenderingService
 * 4. Returns the PNG image as ByteArray
 */
class GenerateCodeImageUseCase(
    private val codeHighlighterService: CodeHighlighterService,
    private val imageRenderingService: ImageRenderingService,
) {
    /**
     * Generate a PNG image from the provided code snippet
     *
     * @param snippet The code to be highlighted and rendered
     * @param config Configuration for image dimensions, colors, fonts, etc.
     * @return PNG image as ByteArray
     */
    fun execute(
        snippet: CodeSnippet,
        config: ImageConfiguration = ImageConfiguration.default(),
    ): ByteArray {
        // Step 1: Apply syntax highlighting to the code
        val highlightedCode = codeHighlighterService.highlight(snippet)

        // Step 2: Render the highlighted code as PNG image
        return imageRenderingService.renderToPng(highlightedCode, config)
    }
}
