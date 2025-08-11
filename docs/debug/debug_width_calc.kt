// Debug the current width calculation for the problematic line
fun debugWidthCalculation() {
    val testLine = "// Test: 5 lines (Small snippets threshold)"
    val LINE_NUMBER_WIDTH = 60
    val CODE_AREA_PADDING = 20
    val configPadding = 0

    // Simulate current calculation
    println("=== DEBUGGING CURRENT WIDTH CALCULATION ===")
    println("Test line: '$testLine'")
    println("Character count: ${testLine.length}")

    // Assume JetBrains Mono 16px gives roughly 9.5px per character
    val estimatedTextWidth = (testLine.length * 9.5).toInt()
    println("Estimated text width: ${estimatedTextWidth}px")

    // Current calculation steps
    val textAreaWidth = estimatedTextWidth
    val layoutWidth = LINE_NUMBER_WIDTH + CODE_AREA_PADDING * 2 + textAreaWidth + configPadding
    println("Layout width: $LINE_NUMBER_WIDTH + ${CODE_AREA_PADDING * 2} + $textAreaWidth + $configPadding = $layoutWidth")

    val characterSafetyMargin = testLine.length * 2
    val percentageSafetyMargin = (layoutWidth * 0.12f).toInt()

    println("Character safety margin: ${testLine.length} * 2 = $characterSafetyMargin")
    println("Percentage safety margin: $layoutWidth * 0.12 = $percentageSafetyMargin")

    val totalSafetyMargin = maxOf(characterSafetyMargin, percentageSafetyMargin, 50)
    println("Total safety margin: maxOf($characterSafetyMargin, $percentageSafetyMargin, 50) = $totalSafetyMargin")

    val baseWidth = layoutWidth + totalSafetyMargin
    println("Base width: $layoutWidth + $totalSafetyMargin = $baseWidth")

    val optimalWidth = maxOf(400 + estimatedTextWidth, baseWidth)
    println("Optimal width: maxOf(${400 + estimatedTextWidth}, $baseWidth) = $optimalWidth")

    // Check if this would be enough
    val actualNeededWidth = LINE_NUMBER_WIDTH + CODE_AREA_PADDING + estimatedTextWidth + 100 // rough estimate
    println("Actually needed width (rough): $actualNeededWidth")
    println("Difference: ${optimalWidth - actualNeededWidth}")
}

fun main() {
    debugWidthCalculation()
}
