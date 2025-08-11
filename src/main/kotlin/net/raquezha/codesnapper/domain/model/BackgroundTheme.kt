package net.raquezha.codesnapper.domain.model

import java.awt.Color

/**
 * BackgroundTheme
 *
 * Predefined background themes for elevated code image appearance.
 * Each theme provides carefully selected colors that complement code windows
 * and provide excellent readability and aesthetic appeal.
 */
enum class BackgroundTheme(
    val baseColor: Color,
    val gradientTopColor: Color,
    val gradientBottomColor: Color,
    val displayName: String,
    val description: String,
) {
    /**
     * IntelliJ Darcula - Dark neutral grays, easy on the eyes
     * Perfect for daily coding and easy on the eyes during long sessions
     */
    DARCULA(
        baseColor = Color(43, 43, 43),
        gradientTopColor = Color(38, 38, 38),
        gradientBottomColor = Color(48, 48, 48),
        displayName = "IntelliJ Darcula",
        description = "Dark neutral grays, easy on the eyes",
    ),

    /**
     * ChatGPT-5 Gradient - Soft blue-green blend, modern and calm
     * Inspired by modern AI interfaces with soothing tones
     */
    CHATGPT5(
        baseColor = Color(0x0C, 0x1E, 0x25),
        gradientTopColor = Color(0x0C, 0x1E, 0x25),
        gradientBottomColor = Color(0x1F, 0x3B, 0x4D),
        displayName = "ChatGPT-5 Gradient",
        description = "Soft blue-green blend, modern and calm",
    ),

    /**
     * Solarized Dark - Classic dev theme with warm accents
     * Popular among developers for its balanced contrast
     */
    SOLARIZED_DARK(
        baseColor = Color(0x00, 0x2B, 0x36),
        gradientTopColor = Color(0x00, 0x2B, 0x36),
        gradientBottomColor = Color(0x07, 0x36, 0x42),
        displayName = "Solarized Dark",
        description = "Classic dev theme with warm accents",
    ),

    /**
     * Nord - Cool Arctic blues and grays
     * Inspired by the Nord color palette, cool and professional
     */
    NORD(
        baseColor = Color(46, 52, 64),
        gradientTopColor = Color(46, 52, 64),
        gradientBottomColor = Color(59, 66, 82),
        displayName = "Nord",
        description = "Cool Arctic blues and grays",
    ),

    /**
     * One Dark Pro - VS Code-inspired balanced dark tone
     * Modern and professional, great for code presentations
     */
    ONE_DARK(
        baseColor = Color(40, 44, 52),
        gradientTopColor = Color(40, 44, 52),
        gradientBottomColor = Color(50, 54, 62),
        displayName = "One Dark Pro",
        description = "VS Code-inspired balanced dark tone",
    ),

    /**
     * Monokai Pro - Slightly warmer dark background
     * Classic coding theme with a warm undertone
     */
    MONOKAI(
        baseColor = Color(39, 40, 34),
        gradientTopColor = Color(39, 40, 34),
        gradientBottomColor = Color(49, 50, 44),
        displayName = "Monokai Pro",
        description = "Slightly warmer dark background",
    ),

    /**
     * Pure White Minimal - Clean, for screenshots or prints
     * Perfect for documentation, presentations, or light themes
     */
    PURE_WHITE(
        baseColor = Color(255, 255, 255),
        gradientTopColor = Color(255, 255, 255),
        gradientBottomColor = Color(245, 245, 245),
        displayName = "Pure White Minimal",
        description = "Clean, for screenshots or prints",
    ),

    /**
     * GitHub Dark - Familiar GitHub dark mode colors
     * Matches the popular GitHub dark theme
     */
    GITHUB_DARK(
        baseColor = Color(13, 17, 23),
        gradientTopColor = Color(13, 17, 23),
        gradientBottomColor = Color(21, 26, 34),
        displayName = "GitHub Dark",
        description = "Familiar GitHub dark mode colors",
    ),
    ;

    companion object {
        /**
         * Get theme by name (case-insensitive)
         */
        fun fromString(name: String?): BackgroundTheme {
            if (name.isNullOrBlank()) return DARCULA

            return entries.find {
                it.name.equals(name, ignoreCase = true) ||
                    it.displayName.equals(name, ignoreCase = true)
            } ?: DARCULA
        }

        /**
         * Get all available theme names for validation
         */
        fun getAllThemeNames(): List<String> = entries.map { it.name.lowercase() }
    }
}
