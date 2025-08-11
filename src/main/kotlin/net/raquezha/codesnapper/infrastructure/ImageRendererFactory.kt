package net.raquezha.codesnapper.infrastructure

import net.raquezha.codesnapper.domain.model.ImageConfiguration
import net.raquezha.codesnapper.domain.service.ImageRenderingService

/**
 * ImageRendererFactory
 *
 * Factory that creates the appropriate image renderer based on design system choice.
 * Supports both macOS-style and Material Design renderers.
 */
class ImageRendererFactory {
    companion object {
        const val DESIGN_SYSTEM_MACOS = "macos"
        const val DESIGN_SYSTEM_MATERIAL = "material"

        val SUPPORTED_DESIGN_SYSTEMS = setOf(DESIGN_SYSTEM_MACOS, DESIGN_SYSTEM_MATERIAL)
    }

    /**
     * Creates the appropriate renderer based on the design system configuration
     */
    fun createRenderer(config: ImageConfiguration): ImageRenderingService {
        return when (config.designSystem.lowercase()) {
            DESIGN_SYSTEM_MATERIAL -> MaterialDesignImageRenderer()
            DESIGN_SYSTEM_MACOS -> Java2DImageRenderer()
            else -> {
                // Fallback to macOS style for unknown design systems
                Java2DImageRenderer()
            }
        }
    }

    /**
     * Validates if the given design system is supported
     */
    fun isValidDesignSystem(designSystem: String?): Boolean {
        return designSystem?.lowercase() in SUPPORTED_DESIGN_SYSTEMS
    }

    /**
     * Normalizes design system string to lowercase
     */
    fun normalizeDesignSystem(designSystem: String?): String {
        val normalized = designSystem?.lowercase() ?: DESIGN_SYSTEM_MACOS
        return if (normalized in SUPPORTED_DESIGN_SYSTEMS) normalized else DESIGN_SYSTEM_MACOS
    }
}
