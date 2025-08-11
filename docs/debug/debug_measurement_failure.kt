import java.awt.*
import java.awt.image.BufferedImage

fun main() {
    // Create a test to see what's really happening
    val tempImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val graphics = tempImage.createGraphics()

    // Use exact same font setup as our app
    val font = Font("JetBrains Mono", Font.PLAIN, 16)
    graphics.font = font
    val fontMetrics = graphics.fontMetrics

    val testLine = "// Test: 5 lines (Small snippets threshold)"

    println("=== DEBUGGING TEXT MEASUREMENT FAILURE ===")
    println("Test line: '$testLine'")
    println("Length: ${testLine.length} characters")

    // Test both methods
    val stringWidth = fontMetrics.stringWidth(testLine)
    val stringBounds = fontMetrics.getStringBounds(testLine, graphics)

    println("stringWidth(): ${stringWidth}px")
    println("getStringBounds(): ${stringBounds.width}px")
    println("Difference: ${kotlin.math.abs(stringWidth - stringBounds.width)}px")

    // Simulate our current calculation
    val LINE_NUMBER_WIDTH = 60
    val CODE_AREA_PADDING = 20
    val configPadding = 0

    val maxLineWidth = stringBounds.width.toInt()
    val textAreaWidth = maxLineWidth
    val layoutWidth = LINE_NUMBER_WIDTH + CODE_AREA_PADDING * 2 + textAreaWidth + configPadding

    val characterSafetyMargin = testLine.length * 2
    val percentageSafetyMargin = (layoutWidth * 0.15f).toInt()
    val combinedSafetyMargin = characterSafetyMargin + percentageSafetyMargin + 50
    val baseWidth = layoutWidth + combinedSafetyMargin
    val optimalWidth = maxOf(400 + maxLineWidth, baseWidth)

    println("\n=== CURRENT CALCULATION ===")
    println("maxLineWidth (getStringBounds): ${maxLineWidth}px")
    println("layoutWidth: ${layoutWidth}px")
    println("characterSafetyMargin: ${characterSafetyMargin}px")
    println("percentageSafetyMargin: ${percentageSafetyMargin}px")
    println("combinedSafetyMargin: ${combinedSafetyMargin}px")
    println("baseWidth: ${baseWidth}px")
    println("optimalWidth: ${optimalWidth}px")

    // Calculate where text actually starts
    val textStartX = LINE_NUMBER_WIDTH + CODE_AREA_PADDING
    val availableTextSpace = optimalWidth - textStartX - CODE_AREA_PADDING

    println("\n=== SPACE ANALYSIS ===")
    println("Text starts at X: ${textStartX}px")
    println("Available space for text: ${availableTextSpace}px")
    println("Text needs: ${maxLineWidth}px")
    println("Overflow: ${if (maxLineWidth > availableTextSpace) "YES by ${maxLineWidth - availableTextSpace}px" else "NO"}")

    graphics.dispose()
}
