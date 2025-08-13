package net.raquezha.codesnapper.controller

import kotlinx.serialization.Serializable

/**
 * SnapRequest
 *
 * API request model for the /snap endpoint.
 * Includes code content, language/theme settings, and optional image configuration.
 */
@Serializable
data class SnapRequest(
    val code: String,
    val language: String,
    val theme: String? = "darcula",
    val darkMode: Boolean = true, // retained for backward compatibility (syntax highlighting darkness)
    // Unified configuration preset: "default", "light", "presentation", "compact"
    val preset: String? = null,
    // Optional title for the code window
    val title: String? = null,
    // Optional custom filename (without extension)
    val filename: String? = null,
    // Background theme for the elevated canvas
    val backgroundTheme: String? = "darcula",
    // Design system choice: "macos" or "material"
    val designSystem: String? = "macos",
    // Optional image configuration parameters (override preset)
    val width: Int? = null,
    val height: Int? = null,
    val fontSize: Int? = null,
    val fontFamily: String? = null,
    val padding: Int? = null,
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val lineHeight: Double? = null,
)
