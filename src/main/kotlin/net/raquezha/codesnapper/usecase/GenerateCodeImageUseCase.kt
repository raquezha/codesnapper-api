package net.raquezha.codesnapper.usecase

import net.raquezha.codesnapper.controller.SnapRequest
import net.raquezha.codesnapper.domain.model.BackgroundTheme
import net.raquezha.codesnapper.domain.model.CodeSnippet
import net.raquezha.codesnapper.domain.model.ImageConfiguration
import net.raquezha.codesnapper.domain.service.CodeHighlighterService
import net.raquezha.codesnapper.infrastructure.ImageRendererFactory

/**
 * GenerateCodeImageUseCase
 *
 * Orchestrates the complete process of converting code input into a PNG image:
 * 1. Takes a CodeSnippet and ImageConfiguration
 * 2. Applies syntax highlighting via CodeHighlighterService
 * 3. Creates appropriate renderer (macOS or Material Design) via ImageRendererFactory
 * 4. Renders the highlighted code as PNG via the selected renderer
 * 5. Returns the PNG image as ByteArray
 */
class GenerateCodeImageUseCase(
    private val codeHighlighterService: CodeHighlighterService,
    private val imageRendererFactory: ImageRendererFactory,
) {
    /**
     * Generate a PNG image from the provided code snippet
     *
     * @param snippet The code to be highlighted and rendered
     * @param config Configuration for image dimensions, colors, fonts, design system, etc.
     * @return PNG image as ByteArray
     */
    fun execute(
        snippet: CodeSnippet,
        config: ImageConfiguration = ImageConfiguration.default(),
    ): ByteArray {
        // Step 1: Apply syntax highlighting to the code
        val highlightedCode = codeHighlighterService.highlight(snippet)

        // Step 2: Create appropriate renderer based on design system choice
        val imageRenderer = imageRendererFactory.createRenderer(config)

        // Step 3: Render the highlighted code as PNG image
        return imageRenderer.renderToPng(highlightedCode, config)
    }

    /**
     * Generate a PNG image from a SnapRequest with optional image configuration overrides
     *
     * @param request The API request containing code and optional configuration
     * @return PNG image as ByteArray
     */
    fun execute(request: SnapRequest): ByteArray {
        val snippet =
            CodeSnippet(
                code = request.code,
                language = request.language,
                theme = request.theme ?: "darcula",
                darkMode = request.darkMode,
            )

        val config = createImageConfiguration(request)

        return execute(snippet, config)
    }

    private fun createImageConfiguration(request: SnapRequest): ImageConfiguration {
        // Determine base configuration from preset (default, light, presentation, compact)
        val base =
            when (request.preset?.lowercase()) {
                "light" -> ImageConfiguration.lightTheme()
                "presentation" -> ImageConfiguration.presentation()
                "compact" -> ImageConfiguration.compact()
                else -> ImageConfiguration.default()
            }

        return ImageConfiguration(
            width = request.width ?: base.width,
            height = request.height ?: base.height,
            fontSize = request.fontSize ?: base.fontSize,
            fontFamily = request.fontFamily ?: base.fontFamily,
            padding = request.padding ?: base.padding,
            backgroundColor = request.backgroundColor ?: base.backgroundColor,
            textColor = request.textColor ?: base.textColor,
            lineHeight = request.lineHeight ?: base.lineHeight,
            // Pass through the optional title
            title = request.title,
            // Parse and apply the background theme
            backgroundTheme = BackgroundTheme.fromString(request.backgroundTheme),
            // Pass through the design system choice
            designSystem = request.designSystem ?: base.designSystem,
        )
    }
}
