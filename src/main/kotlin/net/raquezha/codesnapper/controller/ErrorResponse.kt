package net.raquezha.codesnapper.controller

import kotlinx.serialization.Serializable
import net.raquezha.codesnapper.domain.model.ValidationError

/**
 * ErrorResponse
 *
 * Standard error response format for API errors.
 * Provides consistent error messaging across all endpoints.
 */
@Serializable
data class ErrorResponse(
    val error: String,
    val message: String,
    val code: String,
    val timestamp: String = java.time.Instant.now().toString(),
    val details: List<ValidationErrorDto>? = null,
)

/**
 * ValidationErrorDto
 *
 * DTO representation of validation errors for API responses.
 */
@Serializable
data class ValidationErrorDto(
    val field: String,
    val message: String,
    val code: String,
)

/**
 * Extension function to convert domain ValidationError to DTO
 */
fun ValidationError.toDto() =
    ValidationErrorDto(
        field = this.field,
        message = this.message,
        code = this.code,
    )
