package net.raquezha.codesnapper.domain.service

import net.raquezha.codesnapper.controller.SnapRequest
import net.raquezha.codesnapper.domain.model.ValidationError
import net.raquezha.codesnapper.domain.model.ValidationResult

/**
 * InputValidationService
 *
 * Validates API input parameters and returns meaningful error messages.
 * Handles validation for code content, language, theme, and image configuration.
 */
class InputValidationService {
    companion object {
        private const val MAX_CODE_LENGTH = 50_000 // 50KB of code
        private const val MIN_WIDTH = 200
        private const val MAX_WIDTH = 4000
        private const val MIN_HEIGHT = 200
        private const val MAX_HEIGHT = 4000
        private const val MIN_FONT_SIZE = 8
        private const val MAX_FONT_SIZE = 72
        private const val MIN_PADDING = 0
        private const val MAX_PADDING = 200

        private val SUPPORTED_LANGUAGES =
            setOf(
                "kotlin", "java", "javascript", "typescript", "python", "rust", "go",
                "swift", "c", "cpp", "csharp", "php", "ruby", "scala", "sql", "json",
                "xml", "yaml", "markdown", "bash", "shell", "dockerfile", "html", "css",
            )

        private val SUPPORTED_THEMES =
            setOf(
                "darcula",
                "monokai",
                "notepad",
                "matrix",
                "pastel",
                "atom",
                "atomone",
                "atom_one",
            )

        private val SUPPORTED_DESIGN_SYSTEMS =
            setOf("macos", "material")
    }

    /**
     * Validate a SnapRequest and return validation result
     */
    fun validate(request: SnapRequest): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate code content
        validateCode(request.code, errors)

        // Validate language
        validateLanguage(request.language, errors)

        // Validate theme
        validateTheme(request.theme, errors)

        // Validate design system
        validateDesignSystem(request.designSystem, errors)

        // Validate optional image configuration parameters
        request.width?.let { validateWidth(it, errors) }
        request.height?.let { validateHeight(it, errors) }
        request.fontSize?.let { validateFontSize(it, errors) }
        request.padding?.let { validatePadding(it, errors) }
        request.backgroundColor?.let { validateColor(it, "backgroundColor", errors) }
        request.textColor?.let { validateColor(it, "textColor", errors) }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun validateCode(
        code: String?,
        errors: MutableList<ValidationError>,
    ) {
        when {
            code.isNullOrBlank() -> {
                errors.add(
                    ValidationError(
                        field = "code",
                        message = "Code cannot be empty or blank",
                        code = "EMPTY_CODE",
                    ),
                )
            }
            code.length > MAX_CODE_LENGTH -> {
                errors.add(
                    ValidationError(
                        field = "code",
                        message = "Code exceeds maximum length of $MAX_CODE_LENGTH characters",
                        code = "CODE_TOO_LONG",
                    ),
                )
            }
        }
    }

    private fun validateLanguage(
        language: String?,
        errors: MutableList<ValidationError>,
    ) {
        when {
            language.isNullOrBlank() -> {
                errors.add(
                    ValidationError(
                        field = "language",
                        message = "Language is required",
                        code = "MISSING_LANGUAGE",
                    ),
                )
            }
            !SUPPORTED_LANGUAGES.contains(language.lowercase()) -> {
                errors.add(
                    ValidationError(
                        field = "language",
                        message = "Unsupported language: $language. Supported languages: ${SUPPORTED_LANGUAGES.joinToString(
                            ", ",
                        )}",
                        code = "UNSUPPORTED_LANGUAGE",
                    ),
                )
            }
        }
    }

    private fun validateTheme(
        theme: String?,
        errors: MutableList<ValidationError>,
    ) {
        if (!theme.isNullOrBlank() && !SUPPORTED_THEMES.contains(theme.lowercase())) {
            errors.add(
                ValidationError(
                    field = "theme",
                    message = "Unsupported theme: $theme. Supported themes: ${SUPPORTED_THEMES.joinToString(", ")}",
                    code = "UNSUPPORTED_THEME",
                ),
            )
        }
    }

    private fun validateDesignSystem(
        designSystem: String?,
        errors: MutableList<ValidationError>,
    ) {
        if (!designSystem.isNullOrBlank() && !SUPPORTED_DESIGN_SYSTEMS.contains(designSystem.lowercase())) {
            errors.add(
                ValidationError(
                    field = "designSystem",
                    message = "Unsupported design system: $designSystem. Supported design systems: ${SUPPORTED_DESIGN_SYSTEMS.joinToString(
                        ", ",
                    )}",
                    code = "UNSUPPORTED_DESIGN_SYSTEM",
                ),
            )
        }
    }

    private fun validateWidth(
        width: Int,
        errors: MutableList<ValidationError>,
    ) {
        if (width < MIN_WIDTH || width > MAX_WIDTH) {
            errors.add(
                ValidationError(
                    field = "width",
                    message = "Width must be between $MIN_WIDTH and $MAX_WIDTH pixels",
                    code = "INVALID_WIDTH",
                ),
            )
        }
    }

    private fun validateHeight(
        height: Int,
        errors: MutableList<ValidationError>,
    ) {
        if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
            errors.add(
                ValidationError(
                    field = "height",
                    message = "Height must be between $MIN_HEIGHT and $MAX_HEIGHT pixels",
                    code = "INVALID_HEIGHT",
                ),
            )
        }
    }

    private fun validateFontSize(
        fontSize: Int,
        errors: MutableList<ValidationError>,
    ) {
        if (fontSize < MIN_FONT_SIZE || fontSize > MAX_FONT_SIZE) {
            errors.add(
                ValidationError(
                    field = "fontSize",
                    message = "Font size must be between $MIN_FONT_SIZE and $MAX_FONT_SIZE",
                    code = "INVALID_FONT_SIZE",
                ),
            )
        }
    }

    private fun validatePadding(
        padding: Int,
        errors: MutableList<ValidationError>,
    ) {
        if (padding < MIN_PADDING || padding > MAX_PADDING) {
            errors.add(
                ValidationError(
                    field = "padding",
                    message = "Padding must be between $MIN_PADDING and $MAX_PADDING pixels",
                    code = "INVALID_PADDING",
                ),
            )
        }
    }

    private fun validateColor(
        color: String,
        fieldName: String,
        errors: MutableList<ValidationError>,
    ) {
        val hexColorRegex = Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")
        if (!hexColorRegex.matches(color)) {
            errors.add(
                ValidationError(
                    field = fieldName,
                    message = "Invalid color format. Expected hex color (e.g., #ffffff or #ffffffff)",
                    code = "INVALID_COLOR_FORMAT",
                ),
            )
        }
    }
}
