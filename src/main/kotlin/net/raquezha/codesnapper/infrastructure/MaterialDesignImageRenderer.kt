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
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * MaterialDesignImageRenderer
 *
 * Material Design compliant implementation of ImageRenderingService.
 * Follows Google's Material Design 3 specifications for cards, elevation, colors, and typography.
 *
 * Key Material Design principles implemented:
 * - Proper elevation levels (2dp, 4dp, 8dp, 16dp)
 * - Material Design 3 color tokens
 * - 8dp grid system
 * - Material typography scale
 * - Card-based layout patterns
 * - Floating Action Button patterns
 */
class MaterialDesignImageRenderer : ImageRenderingService {
    companion object {
        private const val HEX_COLOR_LENGTH_6 = 6
        private const val HEX_COLOR_LENGTH_8 = 8
        private const val HEX_RADIX = 16

        // Material Design 3 constants
        private const val MATERIAL_CORNER_RADIUS = 12 // Material Design 3 large corner radius
        private const val MATERIAL_HEADER_HEIGHT = 56 // Material toolbar height
        private const val MATERIAL_GRID_UNIT = 8 // Base 8dp grid system
        private const val LINE_NUMBER_WIDTH = 64 // Aligned to 8dp grid
        private const val CODE_AREA_PADDING = 16 // 2 * 8dp

        // Material Design elevation levels (converted to pixels for shadow)
        private const val ELEVATION_LEVEL_2 = 2 // Cards
        private const val ELEVATION_LEVEL_4 = 4 // App bars, buttons
        private const val ELEVATION_LEVEL_8 = 8 // Navigation drawer, modal bottom sheet
        private const val ELEVATION_LEVEL_16 = 16 // FABs
        private const val OUTER_MARGIN = 80 // 10 * 8dp

        // Material Design 3 Color Tokens
        private val MD3_SURFACE_LIGHT = Color(255, 251, 254) // md.sys.color.surface
        private val MD3_SURFACE_DARK = Color(16, 20, 24) // md.sys.color.surface
        private val MD3_SURFACE_VARIANT_LIGHT = Color(231, 224, 236) // md.sys.color.surface-variant
        private val MD3_SURFACE_VARIANT_DARK = Color(73, 69, 79) // md.sys.color.surface-variant
        private val MD3_ON_SURFACE_LIGHT = Color(28, 27, 31) // md.sys.color.on-surface
        private val MD3_ON_SURFACE_DARK = Color(230, 225, 229) // md.sys.color.on-surface
        private val MD3_PRIMARY_LIGHT = Color(103, 80, 164) // md.sys.color.primary
        private val MD3_PRIMARY_DARK = Color(208, 188, 255) // md.sys.color.primary
        private val MD3_OUTLINE_LIGHT = Color(121, 116, 126) // md.sys.color.outline
        private val MD3_OUTLINE_DARK = Color(147, 143, 153) // md.sys.color.outline

        // Material Design shadow colors
        private val MD3_SHADOW_COLOR = Color(0, 0, 0, 40) // Standard shadow
        private val MD3_KEY_SHADOW = Color(0, 0, 0, 30) // Key light shadow
        private val MD3_AMBIENT_SHADOW = Color(0, 0, 0, 15) // Ambient shadow
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

        // Add outer margins for Material Design elevation
        val cardWidth = optimalWidth
        val cardHeight = optimalHeight + MATERIAL_HEADER_HEIGHT
        val canvasWidth = cardWidth + (OUTER_MARGIN * 2)
        val canvasHeight = cardHeight + (OUTER_MARGIN * 2)

        // Use 2x scaling for high-DPI displays (Material Design recommendation)
        val scaleFactor = 2.0
        val finalWidth = (canvasWidth * scaleFactor).toInt()
        val finalHeight = (canvasHeight * scaleFactor).toInt()

        // Create high-resolution buffered image
        val image = BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()

        try {
            // Scale up for high-DPI rendering
            graphics.scale(scaleFactor, scaleFactor)

            // Fill background with Material Design surface color
            drawMaterialBackground(graphics, canvasWidth, canvasHeight, config)

            // Configure Material Design rendering quality
            setupMaterialRenderingHints(graphics)

            // Translate to center the card within the canvas
            graphics.translate(OUTER_MARGIN, OUTER_MARGIN)

            // Draw Material Design elevation shadows
            drawMaterialElevation(graphics, cardWidth, cardHeight, ELEVATION_LEVEL_8)

            // Draw the Material Design card
            drawMaterialCard(graphics, cardWidth, cardHeight, config)

            // Draw Material Design header
            drawMaterialHeader(graphics, cardWidth, config)

            // Draw code content with Material Design styling
            drawMaterialCodeContent(graphics, highlightedCode, config, cardWidth, cardHeight)
        } finally {
            graphics.dispose()
        }

        // Convert to PNG bytes
        return convertToPngBytes(image)
    }

    private fun setupMaterialRenderingHints(graphics: Graphics2D) {
        // Material Design recommended rendering quality
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        graphics.setRenderingHint(
            RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
        )
    }

    private fun drawMaterialBackground(
        graphics: Graphics2D,
        width: Int,
        height: Int,
        config: ImageConfiguration,
    ) {
        // Material Design background - use theme's surface color or fallback to MD3 tokens
        val isDarkTheme = parseColor(config.backgroundColor).red < 128
        val surfaceColor = if (isDarkTheme) MD3_SURFACE_DARK else MD3_SURFACE_LIGHT

        graphics.color = surfaceColor
        graphics.fillRect(0, 0, width, height)
    }

    private fun drawMaterialElevation(
        graphics: Graphics2D,
        width: Int,
        height: Int,
        elevation: Int,
    ) {
        // Material Design elevation shadows
        // Follows Material Design 3 shadow specifications

        // Key shadow (directional)
        graphics.color = MD3_KEY_SHADOW
        val keyOffset = elevation / 2
        graphics.fillRoundRect(
            keyOffset,
            keyOffset * 2,
            width,
            height,
            MATERIAL_CORNER_RADIUS,
            MATERIAL_CORNER_RADIUS,
        )

        // Ambient shadow (omni-directional)
        graphics.color = MD3_AMBIENT_SHADOW
        val ambientOffset = elevation / 4
        graphics.fillRoundRect(
            ambientOffset,
            ambientOffset,
            width,
            height,
            MATERIAL_CORNER_RADIUS,
            MATERIAL_CORNER_RADIUS,
        )
    }

    private fun drawMaterialCard(
        graphics: Graphics2D,
        width: Int,
        height: Int,
        config: ImageConfiguration,
    ) {
        // Material Design card with proper corner radius
        val cardShape =
            RoundRectangle2D.Float(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                MATERIAL_CORNER_RADIUS.toFloat(),
                MATERIAL_CORNER_RADIUS.toFloat(),
            )

        // Fill card background
        graphics.color = parseColor(config.backgroundColor)
        graphics.fill(cardShape)

        // Material Design card outline
        val isDarkTheme = parseColor(config.backgroundColor).red < 128
        val outlineColor = if (isDarkTheme) MD3_OUTLINE_DARK else MD3_OUTLINE_LIGHT
        graphics.color = outlineColor
        graphics.stroke = java.awt.BasicStroke(1f)
        graphics.draw(cardShape)
    }

    private fun drawMaterialHeader(
        graphics: Graphics2D,
        width: Int,
        config: ImageConfiguration,
    ) {
        val isDarkTheme = parseColor(config.backgroundColor).red < 128

        // Material Design header background
        val headerColor = if (isDarkTheme) MD3_SURFACE_VARIANT_DARK else MD3_SURFACE_VARIANT_LIGHT
        graphics.color = headerColor

        // Create header shape with rounded corners only on top
        val headerShape =
            RoundRectangle2D.Float(
                0f,
                0f,
                width.toFloat(),
                MATERIAL_HEADER_HEIGHT.toFloat(),
                MATERIAL_CORNER_RADIUS.toFloat(),
                MATERIAL_CORNER_RADIUS.toFloat(),
            )

        // Fill header
        graphics.fill(headerShape)

        // Clip bottom to create flat bottom edge
        val clipRect =
            java.awt.Rectangle(
                0,
                MATERIAL_CORNER_RADIUS,
                width,
                MATERIAL_HEADER_HEIGHT - MATERIAL_CORNER_RADIUS,
            )
        val oldClip = graphics.clip
        graphics.clip = clipRect
        graphics.fillRect(0, MATERIAL_CORNER_RADIUS, width, MATERIAL_HEADER_HEIGHT - MATERIAL_CORNER_RADIUS)
        graphics.clip = oldClip

        // Draw Material Design divider
        val dividerColor = if (isDarkTheme) MD3_OUTLINE_DARK else MD3_OUTLINE_LIGHT
        graphics.color = dividerColor
        graphics.stroke = java.awt.BasicStroke(1f)
        graphics.drawLine(0, MATERIAL_HEADER_HEIGHT - 1, width, MATERIAL_HEADER_HEIGHT - 1)

        // Draw Material Design action buttons
        drawMaterialActions(graphics, width, isDarkTheme)

        // Draw title text using Material Design typography
        drawMaterialTitle(graphics, width, config, isDarkTheme)
    }

    private fun drawMaterialActions(
        graphics: Graphics2D,
        width: Int,
        isDarkTheme: Boolean,
    ) {
        // Material Design action buttons (instead of macOS traffic lights)
        val actionY = MATERIAL_HEADER_HEIGHT / 2
        val actionSize = 24 // Material icon size
        val actionMargin = 16 // Material margin

        val primaryColor = if (isDarkTheme) MD3_PRIMARY_DARK else MD3_PRIMARY_LIGHT

        // Close action (X)
        graphics.color = primaryColor
        val closeX = width - actionMargin - actionSize
        graphics.stroke = java.awt.BasicStroke(2f)
        graphics.drawLine(closeX, actionY - 6, closeX + 12, actionY + 6)
        graphics.drawLine(closeX + 12, actionY - 6, closeX, actionY + 6)

        // Minimize action (-)
        val minimizeX = closeX - actionSize - 8
        graphics.drawLine(minimizeX, actionY, minimizeX + 12, actionY)

        // Fullscreen action (□)
        val fullscreenX = minimizeX - actionSize - 8
        graphics.stroke = java.awt.BasicStroke(1.5f)
        graphics.drawRect(fullscreenX, actionY - 6, 12, 12)
    }

    private fun drawMaterialTitle(
        graphics: Graphics2D,
        width: Int,
        config: ImageConfiguration,
        isDarkTheme: Boolean,
    ) {
        val titleText = config.title
        if (titleText.isNullOrBlank()) {
            return
        }

        // Material Design typography - Headline Small
        val textColor = if (isDarkTheme) MD3_ON_SURFACE_DARK else MD3_ON_SURFACE_LIGHT
        graphics.color = textColor
        graphics.font = Font("Roboto", Font.BOLD, 16) // Material typography scale - use BOLD instead of MEDIUM

        val fontMetrics = graphics.fontMetrics
        val x = 16 // Material margin
        val y = MATERIAL_HEADER_HEIGHT / 2 + fontMetrics.ascent / 2 - 1

        graphics.drawString(titleText, x, y)
    }

    private fun drawMaterialCodeContent(
        graphics: Graphics2D,
        highlightedCode: HighlightedCode,
        config: ImageConfiguration,
        cardWidth: Int,
        cardHeight: Int,
    ) {
        // Setup Material Design code font (Roboto Mono)
        val codeFont = createMaterialCodeFont(config.fontFamily, config.fontSize)
        graphics.font = codeFont
        val fontMetrics = graphics.fontMetrics

        val codeAreaTop = MATERIAL_HEADER_HEIGHT + CODE_AREA_PADDING
        val lineHeight = (fontMetrics.height * config.lineHeight).toInt()

        val lines = highlightedCode.code.split('\n')

        // Draw Material Design line numbers area
        drawMaterialLineNumbersArea(graphics, lines.size, codeAreaTop, lineHeight, config)

        // Draw line numbers with Material Design styling
        drawMaterialLineNumbers(graphics, lines.size, codeAreaTop, lineHeight, fontMetrics, config)

        // Draw code with Material Design syntax highlighting
        drawMaterialHighlightedCode(graphics, highlightedCode, codeAreaTop, lineHeight, fontMetrics, config)
    }

    private fun drawMaterialLineNumbersArea(
        graphics: Graphics2D,
        lineCount: Int,
        startY: Int,
        lineHeight: Int,
        config: ImageConfiguration,
    ) {
        val isDarkTheme = parseColor(config.backgroundColor).red < 128
        val surfaceVariantColor = if (isDarkTheme) MD3_SURFACE_VARIANT_DARK else MD3_SURFACE_VARIANT_LIGHT
        graphics.color = surfaceVariantColor

        val contentAreaHeight = lineCount * lineHeight + CODE_AREA_PADDING
        graphics.fillRect(
            0,
            MATERIAL_HEADER_HEIGHT,
            LINE_NUMBER_WIDTH,
            contentAreaHeight,
        )

        // Material Design divider
        val outlineColor = if (isDarkTheme) MD3_OUTLINE_DARK else MD3_OUTLINE_LIGHT
        graphics.color = outlineColor
        graphics.drawLine(
            LINE_NUMBER_WIDTH,
            MATERIAL_HEADER_HEIGHT,
            LINE_NUMBER_WIDTH,
            MATERIAL_HEADER_HEIGHT + contentAreaHeight,
        )
    }

    private fun drawMaterialLineNumbers(
        graphics: Graphics2D,
        lineCount: Int,
        startY: Int,
        lineHeight: Int,
        fontMetrics: FontMetrics,
        config: ImageConfiguration,
    ) {
        val isDarkTheme = parseColor(config.backgroundColor).red < 128
        val textColor = if (isDarkTheme) MD3_ON_SURFACE_DARK else MD3_ON_SURFACE_LIGHT

        // Material Design body medium with reduced opacity
        graphics.color = Color(textColor.red, textColor.green, textColor.blue, 150)
        graphics.font = graphics.font.deriveFont(config.fontSize.toFloat() - 1)

        for (i in 1..lineCount) {
            val lineNumber = i.toString()
            val numberWidth = fontMetrics.stringWidth(lineNumber)
            val x = LINE_NUMBER_WIDTH - numberWidth - 12 // Material margin
            val y = startY + (i - 1) * lineHeight + fontMetrics.ascent

            graphics.drawString(lineNumber, x, y)
        }
    }

    private fun drawMaterialHighlightedCode(
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

            val lineStart = absolutePos
            val lineEnd = absolutePos + lineContent.length

            val lineHighlights =
                highlights.filter { highlight ->
                    highlight.location.start < lineEnd && highlight.location.end > lineStart
                }

            renderMaterialLineWithHighlights(
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
            absolutePos += lineContent.length + 1
        }
    }

    private fun renderMaterialLineWithHighlights(
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

        // Default Material Design text color
        val isDarkTheme = parseColor(config.backgroundColor).red < 128
        val defaultColor = if (isDarkTheme) MD3_ON_SURFACE_DARK else MD3_ON_SURFACE_LIGHT
        graphics.color = defaultColor

        if (highlights.isEmpty()) {
            graphics.drawString(line, x, y)
            return
        }

        val sortedHighlights = highlights.sortedBy { it.location.start }

        for (highlight in sortedHighlights) {
            val relativeStart = maxOf(0, highlight.location.start - lineStart)
            val relativeEnd = minOf(line.length, highlight.location.end - lineStart)

            if (relativeStart > pos) {
                val beforeText = line.substring(pos, relativeStart)
                graphics.color = defaultColor
                graphics.drawString(beforeText, x, y)
                x += fontMetrics.stringWidth(beforeText)
            }

            if (relativeEnd > relativeStart) {
                val highlightedText = line.substring(relativeStart, relativeEnd)
                applyMaterialHighlightStyle(graphics, highlight, config)
                graphics.drawString(highlightedText, x, y)
                x += fontMetrics.stringWidth(highlightedText)
                pos = relativeEnd
            }
        }

        if (pos < line.length) {
            val remainingText = line.substring(pos)
            graphics.color = defaultColor
            graphics.drawString(remainingText, x, y)
        }
    }

    private fun applyMaterialHighlightStyle(
        graphics: Graphics2D,
        highlight: CodeHighlight,
        config: ImageConfiguration,
    ) {
        when (highlight) {
            is ColorHighlight -> graphics.color = Color(highlight.rgb)
            is BoldHighlight -> {
                val isDarkTheme = parseColor(config.backgroundColor).red < 128
                val textColor = if (isDarkTheme) MD3_ON_SURFACE_DARK else MD3_ON_SURFACE_LIGHT
                graphics.color = textColor
                graphics.font = graphics.font.deriveFont(Font.BOLD)
            }
        }
    }

    private fun createMaterialCodeFont(
        fontFamily: String,
        fontSize: Int,
    ): Font {
        // Material Design recommends Roboto Mono for code
        val materialFontNames =
            listOf(
                fontFamily,
                "Roboto Mono",
                "JetBrains Mono",
                "Fira Code",
                "SF Mono",
                Font.MONOSPACED,
            )

        for (fontName in materialFontNames) {
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

    private fun calculateOptimalDimensions(
        highlightedCode: HighlightedCode,
        config: ImageConfiguration,
    ): Pair<Int, Int> {
        // Material Design optimal dimensions using 8dp grid system
        val tempImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val tempGraphics = tempImage.createGraphics()

        try {
            setupMaterialRenderingHints(tempGraphics)
            val codeFont = createMaterialCodeFont(config.fontFamily, config.fontSize)
            tempGraphics.font = codeFont
            val fontMetrics = tempGraphics.fontMetrics

            val lines = highlightedCode.code.split('\n')
            val lineHeight = (fontMetrics.height * config.lineHeight).toInt()

            val maxLineWidth =
                lines.maxOfOrNull { line ->
                    val stringWidth = fontMetrics.stringWidth(line)
                    val boundsWidth = fontMetrics.getStringBounds(line, tempGraphics).width.toInt()
                    val charAdvanceWidth = line.toCharArray().sumOf { fontMetrics.charWidth(it) }
                    maxOf(stringWidth, boundsWidth, charAdvanceWidth)
                } ?: 0

            // Material Design spacing - align to 8dp grid
            val longestLineLength = lines.maxOfOrNull { it.length } ?: 0
            val materialPaddingPercentage =
                when {
                    longestLineLength > 50 -> 0.30
                    longestLineLength > 30 -> 0.25
                    longestLineLength > 20 -> 0.20
                    else -> 0.15
                }

            val dynamicPadding = (maxLineWidth * materialPaddingPercentage).toInt()
            val leftSpace = LINE_NUMBER_WIDTH + CODE_AREA_PADDING
            val rightSpace = CODE_AREA_PADDING + dynamicPadding
            val totalWidthNeeded = leftSpace + maxLineWidth + rightSpace

            // Material Design breakpoints aligned to 8dp grid
            val materialMinimum =
                when {
                    maxLineWidth > 500 -> 1200 // Aligned to 8dp
                    maxLineWidth > 350 -> 1000
                    maxLineWidth > 250 -> 856 // Closest 8dp multiple
                    maxLineWidth > 150 -> 704
                    else -> 600
                }

            val optimalWidth = maxOf(materialMinimum, totalWidthNeeded)

            // Height calculation aligned to Material Design grid
            val actualContentHeight = lines.size * lineHeight
            val topPadding = CODE_AREA_PADDING
            val bottomPadding = 16 // 2 * 8dp
            val totalContentHeight = actualContentHeight + topPadding + bottomPadding

            val optimalHeight =
                when {
                    lines.size <= 5 -> maxOf(160, totalContentHeight) // 20 * 8dp
                    lines.size <= 15 -> maxOf(192, totalContentHeight) // 24 * 8dp
                    lines.size <= 30 -> maxOf(224, totalContentHeight) // 28 * 8dp
                    else -> maxOf(256, totalContentHeight) // 32 * 8dp
                }

            return Pair(optimalWidth, optimalHeight)
        } finally {
            tempGraphics.dispose()
        }
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
}
