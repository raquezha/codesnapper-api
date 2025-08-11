import java.awt.Font
import java.awt.FontMetrics
import java.awt.image.BufferedImage

fun main() {
    // Create graphics context for measurement
    val tempImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val graphics = tempImage.createGraphics()

    // Test the exact same font configuration as our app
    val font = Font("JetBrains Mono", Font.PLAIN, 16)
    graphics.font = font
    val fontMetrics = graphics.fontMetrics

    // Test the problematic line
    val testLine = "// Test: 5 lines (Small snippets threshold)"
    val lineWidth = fontMetrics.stringWidth(testLine)

    println("=== TEXT MEASUREMENT DEBUG ===")
    println("Font: ${font.fontName} ${font.size}px")
    println("Test line: '$testLine'")
    println("Character count: ${testLine.length}")
    println("Measured width: ${lineWidth}px")
    println()

    // Test current calculation components
    val LINE_NUMBER_WIDTH = 60
    val CODE_AREA_PADDING = 20
    val configPadding = 0 // default from ImageConfiguration
    val extraMargin = 150 // current value

    val baseWidth = LINE_NUMBER_WIDTH + CODE_AREA_PADDING * 2 + lineWidth + configPadding + extraMargin
    println("=== CURRENT CALCULATION ===")
    println("LINE_NUMBER_WIDTH: $LINE_NUMBER_WIDTH")
    println("CODE_AREA_PADDING * 2: ${CODE_AREA_PADDING * 2}")
    println("Measured line width: $lineWidth")
    println("Config padding: $configPadding")
    println("Extra margin: $extraMargin")
    println("Total baseWidth: $baseWidth")
    println()

    // Test minimum enforcement
    val currentMinimum = 1000 // for 5 lines
    val finalWidth = maxOf(currentMinimum, baseWidth)
    println("Current minimum (5 lines): $currentMinimum")
    println("Final width: $finalWidth")
    println("Using: ${if (finalWidth == currentMinimum) "MINIMUM" else "CALCULATED"}")
    println()

    // Research other approaches
    println("=== RESEARCH: OTHER APPROACHES ===")

    // 1. Character-based estimation (common in web)
    val avgCharWidth = fontMetrics.stringWidth("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789") / 62
    val charBasedWidth = testLine.length * avgCharWidth
    println("1. Character-based approach:")
    println("   Average char width: ${avgCharWidth}px")
    println("   Estimated width: ${charBasedWidth}px")
    println("   Difference from actual: ${kotlin.math.abs(charBasedWidth - lineWidth)}px")
    println()

    // 2. Viewport percentage approach (responsive design)
    val viewportBasedWidth = (testLine.length * 0.6 * font.size).toInt() // 0.6em per char is common
    println("2. Viewport/em-based approach:")
    println("   0.6em per character: ${viewportBasedWidth}px")
    println("   Difference from actual: ${kotlin.math.abs(viewportBasedWidth - lineWidth)}px")
    println()

    // 3. Safety margin percentage (instead of fixed pixels)
    val safetyMarginPercent = 0.15f // 15% safety margin
    val percentageBasedWidth = (baseWidth * (1 + safetyMarginPercent)).toInt()
    println("3. Percentage-based safety margin:")
    println("   15% safety margin: ${percentageBasedWidth}px")
    println("   Difference from fixed margin: ${kotlin.math.abs(percentageBasedWidth - baseWidth)}px")
    println()

    graphics.dispose()
}
