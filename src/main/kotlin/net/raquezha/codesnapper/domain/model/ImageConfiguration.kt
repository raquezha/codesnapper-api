package net.raquezha.codesnapper.domain.model

/**
 * ImageConfiguration
 *
 * Configuration settings for rendering code images.
 * Controls visual appearance, layout, and styling of the generated PNG images.
 */
data class ImageConfiguration(
    val width: Int = 0, // 0 = auto-size based on content
    val height: Int = 0, // 0 = auto-size based on content
    val padding: Int = 40,
    val fontSize: Int = 18, // Increased from 16 for better readability
    val fontFamily: String = "JetBrains Mono",
    val lineHeight: Double = 1.4,
    val backgroundColor: String = "#1e1e1e",
    val textColor: String = "#ffffff",
    // Optional title for the code window
    val title: String? = null,
    // Background theme for elevated appearance
    val backgroundTheme: BackgroundTheme = BackgroundTheme.DARCULA,
    // Design system choice: "macos" or "material"
    val designSystem: String = "macos",
) {
    companion object {
        /**
         * Default configuration optimized for dark theme code snippets
         */
        fun default() = ImageConfiguration()

        /**
         * Light theme configuration
         */
        fun lightTheme() =
            ImageConfiguration(
                backgroundColor = "#ffffff",
                textColor = "#333333",
            )

        /**
         * Large format configuration for presentations
         */
        fun presentation() =
            ImageConfiguration(
                width = 1920,
                height = 1080,
                padding = 60,
                fontSize = 24,
                lineHeight = 1.5,
            )

        /**
         * Compact configuration for social media
         */
        fun compact() =
            ImageConfiguration(
                width = 800,
                height = 600,
                padding = 20,
                fontSize = 14,
                lineHeight = 1.3,
            )
    }
}
