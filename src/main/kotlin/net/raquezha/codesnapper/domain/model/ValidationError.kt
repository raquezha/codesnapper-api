package net.raquezha.codesnapper.domain.model

/**
 * ValidationError
 *
 * Represents validation errors that can occur during input validation.
 * Used to provide meaningful error messages to API consumers.
 */
data class ValidationError(
    val field: String,
    val message: String,
    val code: String,
)

/**
 * ValidationResult
 *
 * Result of input validation that either succeeds or contains validation errors.
 */
sealed class ValidationResult {
    object Valid : ValidationResult()

    data class Invalid(override val errors: List<ValidationError>) : ValidationResult()

    val isValid: Boolean get() = this is Valid
    open val errors: List<ValidationError> get() =
        when (this) {
            is Valid -> emptyList()
            is Invalid -> this.errors
        }
}
