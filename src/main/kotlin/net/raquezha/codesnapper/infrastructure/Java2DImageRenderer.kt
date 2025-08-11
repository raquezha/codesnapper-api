package net.raquezha.codesnapper.infrastructure

import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.CodeHighlight
import dev.snipme.highlights.model.ColorHighlight
import net.raquezha.codesnapper.domain.model.HighlightedCode
import net.raquezha.codesnapper.domain.model.ImageConfiguration
import net.raquezha.codesnapper.domain.service.ImageRenderingService
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Java2DImageRenderer
 *
 * Implementation of ImageRenderingService using Java2D/Graphics2D for PNG generation.
 * This renderer takes highlighted code and converts it to a PNG image with proper
 * syntax highlighting colors, fonts, and styling.
 */
class Java2DImageRenderer : ImageRenderingService {
    companion object {
        private const val HEX_COLOR_LENGTH_6 = 6
        private const val HEX_COLOR_LENGTH_8 = 8
        private const val HEX_RADIX = 16
    }

    override fun renderToPng(
        highlightedCode: HighlightedCode,
        config: ImageConfiguration,
    ): ByteArray {
        // Create buffered image
        val image = BufferedImage(config.width, config.height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()

        try {
            // Configure rendering quality
            setupRenderingHints(graphics, config)

            // Setup font
            val font = createFont(config.fontFamily, config.fontSize)
            graphics.font = font
            val fontMetrics = graphics.fontMetrics

            // Render highlighted code
            renderHighlightedText(graphics, highlightedCode, config, fontMetrics)
        } finally {
            graphics.dispose()
        }

        // Convert to PNG bytes
        return convertToPngBytes(image)
    }

    private fun setupRenderingHints(
        graphics: Graphics2D,
        config: ImageConfiguration,
    ) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)

        // Set background
        graphics.color = parseColor(config.backgroundColor)
        graphics.fillRect(0, 0, config.width, config.height)
    }

    private fun createFont(
        fontFamily: String,
        fontSize: Int,
    ): Font {
        return try {
            Font(fontFamily, Font.PLAIN, fontSize)
        } catch (ignored: Exception) {
            // Fallback to monospace if custom font fails
            Font(Font.MONOSPACED, Font.PLAIN, fontSize)
        }
    }

    private fun renderHighlightedText(
        graphics: Graphics2D,
        highlightedCode: HighlightedCode,
        config: ImageConfiguration,
        fontMetrics: FontMetrics,
    ) {
        val renderContext =
            RenderContext(
                graphics = graphics,
                config = config,
                fontMetrics = fontMetrics,
                currentX = config.padding,
                currentY = config.padding + fontMetrics.ascent,
            )

        processHighlights(highlightedCode, renderContext)
    }

    private data class RenderContext(
        val graphics: Graphics2D,
        val config: ImageConfiguration,
        val fontMetrics: FontMetrics,
        var currentX: Int,
        var currentY: Int,
    )

    private fun processHighlights(
        highlightedCode: HighlightedCode,
        context: RenderContext,
    ) {
        val code = highlightedCode.code
        val highlights = highlightedCode.highlights.sortedBy { it.location.start }

        var currentPos = 0

        for (highlight in highlights) {
            val start = highlight.location.start
            val end = highlight.location.end

            // Render text before highlight (if any)
            if (start > currentPos) {
                val beforeText = code.substring(currentPos, start)
                context.currentX = renderPlainText(context, beforeText)
            }

            // Render highlighted text
            val highlightedText = code.substring(start, end)
            context.currentX = renderHighlightedSegment(context, highlightedText, highlight)

            currentPos = end
        }

        // Render remaining text (if any)
        if (currentPos < code.length) {
            val remainingText = code.substring(currentPos)
            renderPlainText(context, remainingText)
        }
    }

    private fun renderPlainText(
        context: RenderContext,
        text: String,
    ): Int {
        context.graphics.color = Color.WHITE // Default text color
        return renderText(context, text)
    }

    private fun renderHighlightedSegment(
        context: RenderContext,
        text: String,
        highlight: CodeHighlight,
    ): Int {
        // Set color based on highlight type
        when (highlight) {
            is ColorHighlight -> context.graphics.color = Color(highlight.rgb)
            is BoldHighlight -> {
                context.graphics.color = Color.WHITE
                context.graphics.font = context.graphics.font.deriveFont(Font.BOLD)
            }
            else -> context.graphics.color = Color.WHITE
        }

        val result = renderText(context, text)

        // Reset font if it was made bold
        if (highlight is BoldHighlight) {
            context.graphics.font = context.graphics.font.deriveFont(Font.PLAIN)
        }

        return result
    }

    private fun renderText(
        context: RenderContext,
        text: String,
    ): Int {
        var x = context.currentX
        var y = context.currentY

        for (char in text) {
            if (char == '\n') {
                x = context.config.padding
                y += (context.fontMetrics.height * context.config.lineHeight).toInt()
            } else {
                context.graphics.drawString(char.toString(), x, y)
                x += context.fontMetrics.charWidth(char)
            }
        }

        return x
    }

    private fun parseColor(colorString: String): Color {
        return try {
            when {
                colorString.startsWith("#") -> {
                    val hex = colorString.substring(1)
                    when (hex.length) {
                        HEX_COLOR_LENGTH_6 -> Color(Integer.parseInt(hex, HEX_RADIX))
                        HEX_COLOR_LENGTH_8 -> Color(Integer.parseUnsignedInt(hex, HEX_RADIX), true)
                        else -> Color.BLACK
                    }
                }
                else -> Color.BLACK
            }
        } catch (ignored: NumberFormatException) {
            Color.BLACK
        }
    }

    private fun convertToPngBytes(image: BufferedImage): ByteArray {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "PNG", outputStream)
        return outputStream.toByteArray()
    }
}
