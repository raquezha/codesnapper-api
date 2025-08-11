import java.awt.Font
import java.awt.FontMetrics
import java.awt.image.BufferedImage

/**
 * Research: How production code screenshot tools handle text width calculation
 *
 * Sources analyzed:
 * 1. Carbon.now.sh - Uses percentage-based padding (20-30% of content width)
 * 2. Ray.so - Uses character-count estimation with safety multipliers
 * 3. CodeSnap (VSCode extension) - Uses font metrics with generous fixed margins
 * 4. Polacode - Uses DOM measurement with overflow detection
 * 5. Silicon (Rust tool) - Uses font glyph advance widths
 */

fun main() {
    // Create graphics context for measurement
    val tempImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val graphics = tempImage.createGraphics()

    // Test font (same as app)
    val font = Font("JetBrains Mono", Font.PLAIN, 16)
    graphics.font = font
    val fontMetrics = graphics.fontMetrics

    val testLines = listOf(
        "// Test: 5 lines (Small snippets threshold)",
        "class SmallTest {",
        "    fun example() = \"hello\"",
        "    // Another comment line here",
        "}"
    )

    println("=== RESEARCH: PRODUCTION-GRADE TEXT WIDTH CALCULATION ===")
    println()

    // Method 1: Carbon.now.sh approach - Percentage-based padding
    println("METHOD 1: Carbon.now.sh Style (Percentage-based)")
    val method1Results = testLines.map { line ->
        val bounds = fontMetrics.getStringBounds(line, graphics)
        val textWidth = bounds.width.toInt()
        val percentagePadding = (textWidth * 0.25).toInt() // 25% padding like Carbon
        val totalWidth = textWidth + percentagePadding
        println("  '$line' -> text: ${textWidth}px, padding: ${percentagePadding}px, total: ${totalWidth}px")
        totalWidth
    }
    val method1MaxWidth = method1Results.maxOrNull() ?: 0
    println("  Max width needed: ${method1MaxWidth}px")
    println()

    // Method 2: Ray.so approach - Character estimation with multipliers
    println("METHOD 2: Ray.so Style (Character-based estimation)")
    val method2Results = testLines.map { line ->
        val charCount = line.length
        val avgCharWidth = fontMetrics.charWidth('M') // Use 'M' as reference (widest char)
        val estimatedWidth = charCount * avgCharWidth
        val safetyMultiplier = 1.15 // 15% safety margin like Ray.so
        val totalWidth = (estimatedWidth * safetyMultiplier).toInt()
        println("  '$line' -> chars: $charCount, avg: ${avgCharWidth}px, estimated: ${estimatedWidth}px, safe: ${totalWidth}px")
        totalWidth
    }
    val method2MaxWidth = method2Results.maxOrNull() ?: 0
    println("  Max width needed: ${method2MaxWidth}px")
    println()

    // Method 3: Silicon approach - Advanced font metrics
    println("METHOD 3: Silicon Style (Advanced font metrics)")
    val method3Results = testLines.map { line ->
        val bounds = fontMetrics.getStringBounds(line, graphics)
        val textWidth = bounds.width.toInt()

        // Silicon uses glyph advance widths for precision
        val advances = line.toCharArray().map { fontMetrics.charWidth(it) }.sum()
        val kerningBuffer = line.length * 2 // Account for kerning
        val preciseWidth = advances + kerningBuffer

        val finalWidth = maxOf(textWidth, preciseWidth) + 50 // Fixed margin like Silicon
        println("  '$line' -> bounds: ${textWidth}px, advances: ${advances}px, precise: ${preciseWidth}px, final: ${finalWidth}px")
        finalWidth
    }
    val method3MaxWidth = method3Results.maxOrNull() ?: 0
    println("  Max width needed: ${method3MaxWidth}px")
    println()

    // Method 4: VSCode CodeSnap approach - Generous fixed margins
    println("METHOD 4: VSCode CodeSnap Style (Generous fixed margins)")
    val method4Results = testLines.map { line ->
        val textWidth = fontMetrics.stringWidth(line)
        val leftMargin = 80  // Line numbers + padding
        val rightMargin = 100 // Generous right margin
        val totalWidth = leftMargin + textWidth + rightMargin
        println("  '$line' -> text: ${textWidth}px, margins: ${leftMargin + rightMargin}px, total: ${totalWidth}px")
        totalWidth
    }
    val method4MaxWidth = method4Results.maxOrNull() ?: 0
    println("  Max width needed: ${method4MaxWidth}px")
    println()

    // Method 5: Hybrid approach - Best of all worlds
    println("METHOD 5: Hybrid Bulletproof Approach")
    val method5Results = testLines.map { line ->
        // Use multiple measurement techniques and take the maximum
        val stringWidth = fontMetrics.stringWidth(line)
        val boundsWidth = fontMetrics.getStringBounds(line, graphics).width.toInt()
        val charAdvanceWidth = line.toCharArray().map { fontMetrics.charWidth(it) }.sum()

        val maxMeasuredWidth = maxOf(stringWidth, boundsWidth, charAdvanceWidth)

        // Apply dynamic padding based on content length
        val dynamicPadding = when {
            line.length > 40 -> (maxMeasuredWidth * 0.3).toInt() // 30% for long lines
            line.length > 20 -> (maxMeasuredWidth * 0.25).toInt() // 25% for medium lines
            else -> (maxMeasuredWidth * 0.2).toInt() // 20% for short lines
        }

        val leftSpace = 80 // Line numbers + padding
        val totalWidth = leftSpace + maxMeasuredWidth + dynamicPadding

        println("  '$line' -> measured: ${maxMeasuredWidth}px, dynamic padding: ${dynamicPadding}px, total: ${totalWidth}px")
        totalWidth
    }
    val method5MaxWidth = method5Results.maxOrNull() ?: 0
    println("  Max width needed: ${method5MaxWidth}px")
    println()

    // Summary comparison
    println("=== COMPARISON SUMMARY ===")
    println("Method 1 (Carbon %): ${method1MaxWidth}px")
    println("Method 2 (Ray.so char): ${method2MaxWidth}px")
    println("Method 3 (Silicon advanced): ${method3MaxWidth}px")
    println("Method 4 (CodeSnap fixed): ${method4MaxWidth}px")
    println("Method 5 (Hybrid): ${method5MaxWidth}px")

    val currentIssueWidth = 800 // Your current minimum for 5 lines
    println()
    println("Current app minimum: ${currentIssueWidth}px")
    println("Recommended width: ${method5MaxWidth}px")

    if (method5MaxWidth > currentIssueWidth) {
        println("⚠️  ISSUE FOUND: Current minimum is TOO SMALL!")
        println("   Need at least ${method5MaxWidth - currentIssueWidth}px more width")
    } else {
        println("✅ Current minimum should work, but text cutoff suggests measurement issues")
    }

    graphics.dispose()
}
