package net.raquezha.codesnapper.infrastructure

import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.CodeHighlight
import dev.snipme.highlights.model.ColorHighlight
import net.raquezha.codesnapper.domain.model.BackgroundTheme
import net.raquezha.codesnapper.domain.model.HighlightedCode
import net.raquezha.codesnapper.domain.model.ImageConfiguration
import net.raquezha.codesnapper.domain.service.ImageRenderingService
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Java2DImageRenderer
 *
 * Implementation of ImageRenderingService using Java2D/Graphics2D for PNG generation.
 * Creates beautiful macOS-style code windows with traffic lights, line numbers, and professional styling.
 */
class Java2DImageRenderer : ImageRenderingService {
    companion object {
        private const val HEX_COLOR_LENGTH_6 = 6
        private const val HEX_COLOR_LENGTH_8 = 8
        private const val HEX_RADIX = 16

        // macOS-style design constants - refined for authentic appearance
        private const val WINDOW_BORDER_RADIUS = 14 // More accurate macOS radius
        private const val TITLE_BAR_HEIGHT = 44 // Standard macOS title bar height
        private const val TRAFFIC_LIGHT_SIZE = 14 // More accurate size
        private const val TRAFFIC_LIGHT_SPACING = 20
        private const val TRAFFIC_LIGHT_MARGIN = 20 // Better positioning
        private const val LINE_NUMBER_WIDTH = 60
        private const val CODE_AREA_PADDING = 20

        // Outer margin for elevated appearance
        private const val OUTER_MARGIN = 100 // Increased for better elevation effect

        // macOS-specific colors
        private val MACOS_TITLE_BAR_LIGHT = Color(246, 246, 246, 240) // Semi-transparent light
        private val MACOS_TITLE_BAR_DARK = Color(46, 46, 46, 240) // Semi-transparent dark
        private val MACOS_DIVIDER_LIGHT = Color(200, 200, 200, 120)
        private val MACOS_DIVIDER_DARK = Color(80, 80, 80, 120)

        // Enhanced traffic light colors - more accurate to macOS
        private val TRAFFIC_RED = Color(255, 95, 87)
        private val TRAFFIC_YELLOW = Color(255, 189, 46)
        private val TRAFFIC_GREEN = Color(40, 205, 65)

        // Traffic light hover/active states
        private val TRAFFIC_RED_INNER = Color(255, 255, 255, 50)
        private val TRAFFIC_YELLOW_INNER = Color(255, 255, 255, 50)
        private val TRAFFIC_GREEN_INNER = Color(255, 255, 255, 50)
    }

    override fun renderToPng(
        highlightedCode: HighlightedCode,
        config: ImageConfiguration,
    ): ByteArray {
        // Calculate optimal dimensions based on content if not specified
        val (optimalWidth, optimalHeight) =
            if (config.width == 0 || config.height == 0) {
                calculateOptimalDimensions(highlightedCode, config)
            } else {
                Pair(config.width, config.height)
            }

        // Add outer margins for elevated appearance
        val windowWidth = optimalWidth
        val windowHeight = optimalHeight + TITLE_BAR_HEIGHT
        val canvasWidth = windowWidth + (OUTER_MARGIN * 2)
        val canvasHeight = windowHeight + (OUTER_MARGIN * 2)

        // Use higher DPI for better quality (2x scaling)
        val scaleFactor = 2.0
        val finalWidth = (canvasWidth * scaleFactor).toInt()
        val finalHeight = (canvasHeight * scaleFactor).toInt()

        // Create high-resolution buffered image with outer margins
        val image = BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()

        try {
            // Scale up for high-DPI rendering
            graphics.scale(scaleFactor, scaleFactor)

            // Fill background with subtle gradient or solid color
            drawElevatedBackground(graphics, canvasWidth, canvasHeight, config.backgroundTheme)

            // Configure ultra-high-quality rendering
            setupRenderingHints(graphics)

            // Translate to center the window within the canvas
            graphics.translate(OUTER_MARGIN, OUTER_MARGIN)

            // Add subtle drop shadow for elevation
            drawDropShadow(graphics, windowWidth, windowHeight)

            // Draw the macOS-style window
            drawMacOSWindow(graphics, windowWidth, windowHeight, config)

            // Draw title bar with traffic lights
            drawTitleBar(graphics, windowWidth, config)

            // Draw code content with line numbers
            drawCodeContent(graphics, highlightedCode, config, windowWidth, windowHeight)
        } finally {
            graphics.dispose()
        }

        // Convert to PNG bytes with high quality
        return convertToPngBytes(image)
    }

    private fun setupRenderingHints(graphics: Graphics2D) {
        // Enable high-quality anti-aliasing
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Use LCD text rendering for crisp fonts
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)

        // Enable high-quality rendering
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

        // Use precise font metrics for better text positioning
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)

        // Use pure stroke control for sharp lines
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

        // Enable alpha interpolation for better blending
        graphics.setRenderingHint(
            RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
        )

        // Use bicubic interpolation for scaling
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)

        // Enable color rendering quality
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
    }

    private fun drawMacOSWindow(
        graphics: Graphics2D,
        width: Int,
        height: Int,
        config: ImageConfiguration,
    ) {
        // Create rounded rectangle for window with more refined appearance
        val windowShape =
            RoundRectangle2D.Float(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                WINDOW_BORDER_RADIUS.toFloat(),
                WINDOW_BORDER_RADIUS.toFloat(),
            )

        // Fill window background
        graphics.color = parseColor(config.backgroundColor)
        graphics.fill(windowShape)

        // Add subtle inner border for depth (like real macOS windows)
        graphics.color = Color(255, 255, 255, 15)
        graphics.stroke = java.awt.BasicStroke(1f)
        graphics.draw(windowShape)
    }

    private fun drawTitleBar(
        graphics: Graphics2D,
        width: Int,
        config: ImageConfiguration,
    ) {
        // Determine if we're using a dark or light theme
        val isDarkTheme = parseColor(config.backgroundColor).red < 128

        // Title bar background with authentic macOS appearance
        val titleBarColor = if (isDarkTheme) MACOS_TITLE_BAR_DARK else MACOS_TITLE_BAR_LIGHT
        graphics.color = titleBarColor

        // FIXED: Use RoundRectangle2D instead of custom Path2D to ensure perfect corner alignment
        // This eliminates the empty spaces in the rounded corners
        val titleBarShape =
            RoundRectangle2D.Float(
                0f,
                0f,
                width.toFloat(),
                TITLE_BAR_HEIGHT.toFloat(),
                WINDOW_BORDER_RADIUS.toFloat(),
                WINDOW_BORDER_RADIUS.toFloat(),
            )

        // Fill the entire rounded rectangle first
        graphics.fill(titleBarShape)

        // Then clip the bottom part to create flat bottom edge
        val clipRect = java.awt.Rectangle(0, WINDOW_BORDER_RADIUS, width, TITLE_BAR_HEIGHT - WINDOW_BORDER_RADIUS)
        val oldClip = graphics.clip
        graphics.clip = clipRect

        // Fill the rectangular part to create flat bottom
        graphics.fillRect(0, WINDOW_BORDER_RADIUS, width, TITLE_BAR_HEIGHT - WINDOW_BORDER_RADIUS)

        // Restore original clip
        graphics.clip = oldClip

        // Draw subtle top highlight (like real macOS windows)
        val topHighlight =
            java.awt.GradientPaint(
                0f,
                0f,
                Color(255, 255, 255, 30),
                0f,
                10f,
                Color(255, 255, 255, 0),
            )
        graphics.paint = topHighlight

        // Apply the same clipping technique for the highlight
        graphics.clip = clipRect
        graphics.fill(titleBarShape)
        graphics.clip = oldClip

        // Reset paint to solid color for subsequent operations
        graphics.paint = null

        // Draw bottom divider of title bar with more authentic styling
        val dividerColor = if (isDarkTheme) MACOS_DIVIDER_DARK else MACOS_DIVIDER_LIGHT
        graphics.color = dividerColor
        graphics.stroke = java.awt.BasicStroke(1f)
        graphics.drawLine(0, TITLE_BAR_HEIGHT - 1, width, TITLE_BAR_HEIGHT - 1)

        // Draw traffic lights
        drawTrafficLights(graphics, isDarkTheme)

        // Draw title text
        drawTitleText(graphics, width, config, isDarkTheme)
    }

    private fun drawTrafficLights(
        graphics: Graphics2D,
        isDarkTheme: Boolean,
    ) {
        val y = TITLE_BAR_HEIGHT / 2
        val startX = TRAFFIC_LIGHT_MARGIN

        // Enhanced rendering for traffic lights with authentic styling
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Red traffic light with enhanced styling
        drawTrafficLight(graphics, startX, y, TRAFFIC_RED, TRAFFIC_RED_INNER)

        // Yellow traffic light
        drawTrafficLight(graphics, startX + TRAFFIC_LIGHT_SPACING, y, TRAFFIC_YELLOW, TRAFFIC_YELLOW_INNER)

        // Green traffic light
        drawTrafficLight(graphics, startX + TRAFFIC_LIGHT_SPACING * 2, y, TRAFFIC_GREEN, TRAFFIC_GREEN_INNER)
    }

    private fun drawTrafficLight(
        graphics: Graphics2D,
        x: Int,
        y: Int,
        mainColor: Color,
        innerColor: Color,
    ) {
        val centerY = y - TRAFFIC_LIGHT_SIZE / 2

        // Draw subtle shadow for depth
        graphics.color = Color(0, 0, 0, 30)
        graphics.fillOval(x + 1, centerY + 1, TRAFFIC_LIGHT_SIZE, TRAFFIC_LIGHT_SIZE)

        // Draw main button
        graphics.color = mainColor
        graphics.fillOval(x, centerY, TRAFFIC_LIGHT_SIZE, TRAFFIC_LIGHT_SIZE)

        // Draw subtle inner highlight for 3D effect
        graphics.color = innerColor
        graphics.fillOval(x + 2, centerY + 2, TRAFFIC_LIGHT_SIZE - 6, TRAFFIC_LIGHT_SIZE - 6)

        // Draw subtle outer border
        graphics.color = Color(0, 0, 0, 40)
        graphics.stroke = java.awt.BasicStroke(0.5f)
        graphics.drawOval(x, centerY, TRAFFIC_LIGHT_SIZE, TRAFFIC_LIGHT_SIZE)
    }

    private fun drawTitleText(
        graphics: Graphics2D,
        width: Int,
        config: ImageConfiguration,
        isDarkTheme: Boolean,
    ) {
        // Only draw title if one is provided
        val titleText = config.title
        if (titleText.isNullOrBlank()) {
            return
        }

        // Use system font with proper weight for macOS appearance
        val titleColor = if (isDarkTheme) Color(255, 255, 255, 180) else Color(0, 0, 0, 140)
        graphics.color = titleColor
        graphics.font = Font("SF Pro Display", Font.PLAIN, 13) // More accurate macOS font size

        val fontMetrics = graphics.fontMetrics
        val titleWidth = fontMetrics.stringWidth(titleText)
        val x = (width - titleWidth) / 2
        val y = TITLE_BAR_HEIGHT / 2 + fontMetrics.ascent / 2 - 1

        graphics.drawString(titleText, x, y)
    }

    private fun drawCodeContent(
        graphics: Graphics2D,
        highlightedCode: HighlightedCode,
        config: ImageConfiguration,
        windowWidth: Int,
        windowHeight: Int,
    ) {
        // Setup code font
        val codeFont = createCodeFont(config.fontFamily, config.fontSize)
        graphics.font = codeFont
        val fontMetrics = graphics.fontMetrics

        val codeAreaTop = TITLE_BAR_HEIGHT + CODE_AREA_PADDING
        val lineHeight = (fontMetrics.height * config.lineHeight).toInt()

        val lines = highlightedCode.code.split('\n')

        // Draw line numbers background
        drawLineNumbersBackground(graphics, lines.size, codeAreaTop, lineHeight, config)

        // Draw line numbers
        drawLineNumbers(graphics, lines.size, codeAreaTop, lineHeight, fontMetrics, config)

        // Draw code with syntax highlighting
        drawHighlightedCode(graphics, highlightedCode, codeAreaTop, lineHeight, fontMetrics, config)
    }

    private fun drawLineNumbersBackground(
        graphics: Graphics2D,
        lineCount: Int,
        startY: Int,
        lineHeight: Int,
        config: ImageConfiguration,
    ) {
        val lineNumberBg = darkenColor(parseColor(config.backgroundColor), 0.1f)
        graphics.color = lineNumberBg

        // Calculate the exact height needed for the content area
        val contentAreaHeight = lineCount * lineHeight + CODE_AREA_PADDING

        graphics.fillRect(
            0,
            TITLE_BAR_HEIGHT,
            LINE_NUMBER_WIDTH,
            contentAreaHeight,
        )

        // Draw separator line that matches the content height
        graphics.color = Color(255, 255, 255, 30)
        graphics.drawLine(
            LINE_NUMBER_WIDTH,
            TITLE_BAR_HEIGHT,
            LINE_NUMBER_WIDTH,
            TITLE_BAR_HEIGHT + contentAreaHeight,
        )
    }

    private fun drawLineNumbers(
        graphics: Graphics2D,
        lineCount: Int,
        startY: Int,
        lineHeight: Int,
        fontMetrics: FontMetrics,
        config: ImageConfiguration,
    ) {
        graphics.color = Color(255, 255, 255, 120) // Semi-transparent white
        graphics.font = graphics.font.deriveFont(config.fontSize.toFloat() - 1)

        for (i in 1..lineCount) {
            val lineNumber = i.toString()
            val numberWidth = fontMetrics.stringWidth(lineNumber)
            val x = LINE_NUMBER_WIDTH - numberWidth - 10
            val y = startY + (i - 1) * lineHeight + fontMetrics.ascent

            graphics.drawString(lineNumber, x, y)
        }
    }

    private fun drawHighlightedCode(
        graphics: Graphics2D,
        highlightedCode: HighlightedCode,
        startY: Int,
        lineHeight: Int,
        fontMetrics: FontMetrics,
        config: ImageConfiguration,
    ) {
        val code = highlightedCode.code
        val highlights = highlightedCode.highlights.sortedBy { it.location.start }
        val lines = code.split('\n')

        var currentLine = 0
        var absolutePos = 0

        for (lineContent in lines) {
            val lineStartY = startY + currentLine * lineHeight + fontMetrics.ascent
            val x = LINE_NUMBER_WIDTH + CODE_AREA_PADDING

            // Process highlights for this line
            val lineStart = absolutePos
            val lineEnd = absolutePos + lineContent.length

            val lineHighlights =
                highlights.filter { highlight ->
                    highlight.location.start < lineEnd && highlight.location.end > lineStart
                }

            renderLineWithHighlights(
                graphics,
                lineContent,
                lineHighlights,
                x,
                lineStartY,
                lineStart,
                fontMetrics,
                config,
            )

            currentLine++
            absolutePos += lineContent.length + 1 // +1 for newline
        }
    }

    private fun renderLineWithHighlights(
        graphics: Graphics2D,
        line: String,
        highlights: List<CodeHighlight>,
        startX: Int,
        y: Int,
        lineStart: Int,
        fontMetrics: FontMetrics,
        config: ImageConfiguration,
    ) {
        var x = startX
        var pos = 0

        // Default text color
        graphics.color = parseColor(config.textColor)

        if (highlights.isEmpty()) {
            // No highlights, just draw plain text
            graphics.drawString(line, x, y)
            return
        }

        val sortedHighlights = highlights.sortedBy { it.location.start }

        for (highlight in sortedHighlights) {
            val relativeStart = maxOf(0, highlight.location.start - lineStart)
            val relativeEnd = minOf(line.length, highlight.location.end - lineStart)

            if (relativeStart > pos) {
                // Draw text before highlight
                val beforeText = line.substring(pos, relativeStart)
                graphics.color = parseColor(config.textColor)
                graphics.drawString(beforeText, x, y)
                x += fontMetrics.stringWidth(beforeText)
            }

            if (relativeEnd > relativeStart) {
                // Draw highlighted text
                val highlightedText = line.substring(relativeStart, relativeEnd)
                applyHighlightStyle(graphics, highlight, config)
                graphics.drawString(highlightedText, x, y)
                x += fontMetrics.stringWidth(highlightedText)
                pos = relativeEnd
            }
        }

        // Draw remaining text
        if (pos < line.length) {
            val remainingText = line.substring(pos)
            graphics.color = parseColor(config.textColor)
            graphics.drawString(remainingText, x, y)
        }
    }

    private fun applyHighlightStyle(
        graphics: Graphics2D,
        highlight: CodeHighlight,
        config: ImageConfiguration,
    ) {
        when (highlight) {
            is ColorHighlight -> graphics.color = Color(highlight.rgb)
            is BoldHighlight -> {
                graphics.color = parseColor(config.textColor)
                graphics.font = graphics.font.deriveFont(Font.BOLD)
            }
        }
    }

    private fun createCodeFont(
        fontFamily: String,
        fontSize: Int,
    ): Font {
        val fontNames =
            listOf(
                fontFamily,
                "JetBrains Mono",
                "Fira Code",
                "SF Mono",
                "Menlo",
                "Monaco",
                Font.MONOSPACED,
            )

        for (fontName in fontNames) {
            try {
                val font = Font(fontName, Font.PLAIN, fontSize)
                if (font.family.equals(fontName, ignoreCase = true)) {
                    return font
                }
            } catch (ignored: Exception) {
                // Try next font
            }
        }

        return Font(Font.MONOSPACED, Font.PLAIN, fontSize)
    }

    private fun brightenColor(
        color: Color,
        factor: Float,
    ): Color {
        val r = minOf(255, (color.red + (255 - color.red) * factor).toInt())
        val g = minOf(255, (color.green + (255 - color.green) * factor).toInt())
        val b = minOf(255, (color.blue + (255 - color.blue) * factor).toInt())
        return Color(r, g, b, color.alpha)
    }

    private fun darkenColor(
        color: Color,
        factor: Float,
    ): Color {
        val r = (color.red * (1 - factor)).toInt()
        val g = (color.green * (1 - factor)).toInt()
        val b = (color.blue * (1 - factor)).toInt()
        return Color(r, g, b, color.alpha)
    }

    private fun parseColor(colorString: String): Color {
        return try {
            when {
                colorString.startsWith("#") -> {
                    val hex = colorString.substring(1)
                    when (hex.length) {
                        HEX_COLOR_LENGTH_6 -> Color(Integer.parseInt(hex, HEX_RADIX))
                        HEX_COLOR_LENGTH_8 -> Color(Integer.parseUnsignedInt(hex, HEX_RADIX), true)
                        else -> Color.WHITE
                    }
                }
                else -> Color.WHITE
            }
        } catch (e: NumberFormatException) {
            Color.WHITE
        }
    }

    private fun convertToPngBytes(image: BufferedImage): ByteArray {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "PNG", outputStream)
        return outputStream.toByteArray()
    }

    private fun drawElevatedBackground(
        graphics: Graphics2D,
        width: Int,
        height: Int,
        theme: BackgroundTheme,
    ) {
        // Enhanced background with more sophisticated gradients

        // Apply sophisticated multi-stop gradient for depth
        val gradient =
            java.awt.LinearGradientPaint(
                0f,
                0f,
                0f,
                height.toFloat(),
                floatArrayOf(0.0f, 0.4f, 0.8f, 1.0f),
                arrayOf(
                    theme.gradientTopColor,
                    blendColors(theme.gradientTopColor, theme.baseColor, 0.3f),
                    blendColors(theme.baseColor, theme.gradientBottomColor, 0.5f),
                    theme.gradientBottomColor,
                ),
            )
        graphics.paint = gradient
        graphics.fillRect(0, 0, width, height)

        // Add subtle texture overlay for depth
        graphics.color = Color(255, 255, 255, 3)
        for (i in 0 until height step 4) {
            graphics.drawLine(0, i, width, i)
        }
    }

    private fun blendColors(
        color1: Color,
        color2: Color,
        ratio: Float,
    ): Color {
        val invRatio = 1.0f - ratio
        return Color(
            (color1.red * invRatio + color2.red * ratio).toInt(),
            (color1.green * invRatio + color2.green * ratio).toInt(),
            (color1.blue * invRatio + color2.blue * ratio).toInt(),
            (color1.alpha * invRatio + color2.alpha * ratio).toInt(),
        )
    }

    private fun drawDropShadow(
        graphics: Graphics2D,
        width: Int,
        height: Int,
    ) {
        // Enhanced multi-layered drop shadow for authentic macOS elevation

        // Outer shadow (most diffuse)
        graphics.color = Color(0, 0, 0, 25)
        graphics.fillRoundRect(
            16,
            16,
            width - 32,
            height - 32,
            WINDOW_BORDER_RADIUS + 4,
            WINDOW_BORDER_RADIUS + 4,
        )

        // Middle shadow (medium diffusion)
        graphics.color = Color(0, 0, 0, 40)
        graphics.fillRoundRect(
            12,
            12,
            width - 24,
            height - 24,
            WINDOW_BORDER_RADIUS + 2,
            WINDOW_BORDER_RADIUS + 2,
        )

        // Inner shadow (sharp and close)
        graphics.color = Color(0, 0, 0, 60)
        graphics.fillRoundRect(
            6,
            8,
            width - 12,
            height - 16,
            WINDOW_BORDER_RADIUS,
            WINDOW_BORDER_RADIUS,
        )

        // Very close shadow for definition
        graphics.color = Color(0, 0, 0, 80)
        graphics.fillRoundRect(
            2,
            4,
            width - 4,
            height - 8,
            WINDOW_BORDER_RADIUS,
            WINDOW_BORDER_RADIUS,
        )
    }

    private fun calculateOptimalDimensions(
        highlightedCode: HighlightedCode,
        config: ImageConfiguration,
    ): Pair<Int, Int> {
        // Create a temporary graphics context to measure text accurately
        val tempImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val tempGraphics = tempImage.createGraphics()

        try {
            // Setup font for measurement with same rendering hints as final render
            setupRenderingHints(tempGraphics)
            val codeFont = createCodeFont(config.fontFamily, config.fontSize)
            tempGraphics.font = codeFont
            val fontMetrics = tempGraphics.fontMetrics

            val lines = highlightedCode.code.split('\n')
            val lineHeight = (fontMetrics.height * config.lineHeight).toInt()

            // BULLETPROOF WIDTH CALCULATION
            // Research from Carbon.now.sh, Ray.so, Silicon, and VSCode CodeSnap

            val maxLineWidth =
                lines.maxOfOrNull { line ->
                    // Use MULTIPLE measurement techniques and take the maximum (most accurate)
                    val stringWidth = fontMetrics.stringWidth(line)
                    val boundsWidth = fontMetrics.getStringBounds(line, tempGraphics).width.toInt()
                    val charAdvanceWidth = line.toCharArray().sumOf { fontMetrics.charWidth(it) }

                    // Take the maximum of all measurements for safety
                    maxOf(stringWidth, boundsWidth, charAdvanceWidth)
                } ?: 0

            // Apply DYNAMIC PERCENTAGE-BASED PADDING (Carbon.now.sh approach)
            // This scales with content complexity instead of using fixed margins
            val longestLineLength = lines.maxOfOrNull { it.length } ?: 0
            val dynamicPaddingPercentage =
                when {
                    longestLineLength > 50 -> 0.35 // 35% for very long lines
                    longestLineLength > 30 -> 0.30 // 30% for long lines
                    longestLineLength > 20 -> 0.25 // 25% for medium lines
                    else -> 0.20 // 20% for short lines
                }

            val dynamicPadding = (maxLineWidth * dynamicPaddingPercentage).toInt()

            // Calculate required space components
            val leftSpace = LINE_NUMBER_WIDTH + CODE_AREA_PADDING // 80px total
            val rightSpace = CODE_AREA_PADDING + dynamicPadding // Dynamic based on content
            val totalWidthNeeded = leftSpace + maxLineWidth + rightSpace

            // CONTENT-AWARE MINIMUMS (not arbitrary line count based)
            // Base minimums on actual text complexity, not just line count
            val contentBasedMinimum =
                when {
                    maxLineWidth > 500 -> 1200 // Very wide content needs big windows
                    maxLineWidth > 350 -> 1000 // Wide content
                    maxLineWidth > 250 -> 850 // Medium content
                    maxLineWidth > 150 -> 700 // Narrow content
                    else -> 600 // Very narrow content
                }

            val optimalWidth = maxOf(contentBasedMinimum, totalWidthNeeded)

            // Calculate height with precision
            val actualContentHeight = lines.size * lineHeight
            val topPadding = CODE_AREA_PADDING
            val bottomPadding = 15 // Minimal bottom padding
            val totalContentHeight = actualContentHeight + topPadding + bottomPadding

            val optimalHeight =
                when {
                    lines.size <= 5 -> maxOf(150, totalContentHeight)
                    lines.size <= 15 -> maxOf(170, totalContentHeight)
                    lines.size <= 30 -> maxOf(190, totalContentHeight)
                    else -> maxOf(210, totalContentHeight)
                }

            return Pair(optimalWidth, optimalHeight)
        } finally {
            tempGraphics.dispose()
        }
    }
}
