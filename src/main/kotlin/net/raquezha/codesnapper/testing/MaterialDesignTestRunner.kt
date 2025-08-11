package net.raquezha.codesnapper.testing

import com.google.gson.Gson
import net.raquezha.codesnapper.domain.model.BackgroundTheme
import net.raquezha.codesnapper.domain.model.CodeSnippet
import net.raquezha.codesnapper.domain.model.HighlightedCode
import net.raquezha.codesnapper.domain.model.ImageConfiguration
import net.raquezha.codesnapper.infrastructure.HighlightsCodeHighlighterService
import net.raquezha.codesnapper.infrastructure.MaterialDesignImageRenderer
import java.io.File

/**
 * Material Design Test Runner
 *
 * Runs all Material Design tests and generates compliance reports
 */
class MaterialDesignTestRunner {
    private val gson = Gson()
    private val validator = MaterialDesignValidator()
    private val renderer = MaterialDesignImageRenderer()
    private val highlighter = HighlightsCodeHighlighterService()

    data class TestConfig(
        val code: String,
        val language: String,
        val theme: String,
        val darkMode: Boolean,
        val title: String,
        val filename: String,
        val backgroundTheme: String,
        val fontSize: Int,
        val fontFamily: String,
        val designSystem: String,
    )

    fun runAllTests(testDir: String = "docs/testing"): String {
        println("🎨 Running Material Design Compliance Tests...")

        val testFiles =
            File(testDir).listFiles { file ->
                file.name.endsWith("_material.json")
            } ?: emptyArray()

        if (testFiles.isEmpty()) {
            return "No Material Design test files found in $testDir"
        }

        val results = mutableListOf<Pair<String, MaterialDesignValidator.ValidationResult>>()

        testFiles.forEach { testFile ->
            try {
                println("Testing: ${testFile.name}")

                // Load test configuration
                val testConfig = loadTestConfig(testFile)

                // Generate image using MaterialDesignImageRenderer
                val imageConfig = createImageConfiguration(testConfig)
                val highlightedCode = createHighlightedCode(testConfig.code, testConfig.language)
                val imageBytes = renderer.renderToPng(highlightedCode, imageConfig)

                // Save generated image for validation
                val outputPath = "$testDir/generated/${testConfig.filename}.png"
                saveImage(imageBytes, outputPath)

                // Validate the generated image
                val validationResult = validator.validateImage(outputPath)
                results.add(testFile.nameWithoutExtension to validationResult)

                println("  Score: ${validationResult.score}/100 ${if (validationResult.isCompliant) "✅" else "❌"}")
            } catch (e: Exception) {
                println("  Error testing ${testFile.name}: ${e.message}")
                results.add(
                    testFile.nameWithoutExtension to
                        MaterialDesignValidator.ValidationResult(
                            isCompliant = false,
                            issues = listOf("Test execution failed: ${e.message}"),
                            recommendations = listOf("Fix test configuration"),
                            score = 0,
                        ),
                )
            }
        }

        // Generate comprehensive report
        val report = validator.generateComplianceReport(results)

        // Save report to file
        val reportPath = "$testDir/material_design_compliance_report.txt"
        File(reportPath).writeText(report)
        println("\n📋 Report saved to: $reportPath")

        return report
    }

    private fun loadTestConfig(testFile: File): TestConfig {
        val jsonContent = testFile.readText()
        return gson.fromJson(jsonContent, TestConfig::class.java)
    }

    private fun createImageConfiguration(testConfig: TestConfig): ImageConfiguration {
        // Parse backgroundTheme string to BackgroundTheme enum
        val bgTheme =
            try {
                BackgroundTheme.valueOf(testConfig.backgroundTheme.uppercase())
            } catch (e: IllegalArgumentException) {
                BackgroundTheme.DARCULA // fallback
            }

        return ImageConfiguration(
            title = testConfig.title,
            fontSize = testConfig.fontSize,
            fontFamily = testConfig.fontFamily,
            backgroundTheme = bgTheme,
            designSystem = testConfig.designSystem,
        )
    }

    private fun createHighlightedCode(
        code: String,
        language: String,
    ): HighlightedCode {
        val codeSnippet =
            CodeSnippet(
                code = code,
                language = language,
                // Default theme for Material Design tests
                theme = "darcula",
                darkMode = true,
            )
        return highlighter.highlight(codeSnippet)
    }

    private fun saveImage(
        imageBytes: ByteArray,
        outputPath: String,
    ) {
        val outputFile = File(outputPath)
        outputFile.parentFile.mkdirs()
        outputFile.writeBytes(imageBytes)
    }
}

/**
 * Main function to run Material Design tests
 */
fun main() {
    val testRunner = MaterialDesignTestRunner()
    val report = testRunner.runAllTests()
    println(report)
}
