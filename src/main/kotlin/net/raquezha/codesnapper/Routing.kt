package net.raquezha.codesnapper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import net.raquezha.codesnapper.controller.ErrorResponse
import net.raquezha.codesnapper.controller.SnapRequest
import net.raquezha.codesnapper.controller.toDto
import net.raquezha.codesnapper.domain.model.ValidationResult
import net.raquezha.codesnapper.domain.service.InputValidationService
import net.raquezha.codesnapper.usecase.GenerateCodeImageUseCase
import org.koin.ktor.ext.inject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Application.configureRouting() {
    val generateCodeImageUseCase by inject<GenerateCodeImageUseCase>()
    val inputValidationService by inject<InputValidationService>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        post("/snap") {
            try {
                // Parse request with proper error handling
                val snapRequest =
                    try {
                        call.receive<SnapRequest>()
                    } catch (e: BadRequestException) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(
                                error = "Invalid JSON",
                                message = "Request body must be valid JSON",
                                code = "INVALID_JSON",
                            ),
                        )
                        return@post
                    } catch (e: SerializationException) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(
                                error = "Serialization Error",
                                message = "Invalid request format: ${e.message}",
                                code = "SERIALIZATION_ERROR",
                            ),
                        )
                        return@post
                    }

                // Validate input parameters
                when (val validationResult = inputValidationService.validate(snapRequest)) {
                    is ValidationResult.Invalid -> {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(
                                error = "Validation Failed",
                                message = "Input validation failed",
                                code = "VALIDATION_ERROR",
                                details = validationResult.errors.map { it.toDto() },
                            ),
                        )
                        return@post
                    }
                    is ValidationResult.Valid -> {
                        // Continue with image generation
                    }
                }

                // Generate PNG image with proper error handling
                val imageBytes =
                    try {
                        generateCodeImageUseCase.execute(snapRequest)
                    } catch (e: IllegalArgumentException) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(
                                error = "Invalid Configuration",
                                message = "Image configuration error: ${e.message}",
                                code = "INVALID_CONFIGURATION",
                            ),
                        )
                        return@post
                    } catch (e: OutOfMemoryError) {
                        call.respond(
                            HttpStatusCode.PayloadTooLarge,
                            ErrorResponse(
                                error = "Resource Limit Exceeded",
                                message = "Image too large to generate. Try reducing dimensions or code length.",
                                code = "MEMORY_LIMIT_EXCEEDED",
                            ),
                        )
                        return@post
                    } catch (e: IOException) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse(
                                error = "Image Generation Failed",
                                message = "Failed to generate image: ${e.message}",
                                code = "IMAGE_GENERATION_ERROR",
                            ),
                        )
                        return@post
                    } catch (e: Exception) {
                        // Log the unexpected error for debugging
                        application.log.error("Unexpected error during image generation", e)
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse(
                                error = "Internal Server Error",
                                message = "An unexpected error occurred while generating the image",
                                code = "INTERNAL_ERROR",
                            ),
                        )
                        return@post
                    }

                // Validate generated image
                if (imageBytes.isEmpty()) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(
                            error = "Empty Image",
                            message = "Generated image is empty",
                            code = "EMPTY_IMAGE",
                        ),
                    )
                    return@post
                }

                // Generate standardized filename with timestamp or use custom filename
                val filename = generateFilename(snapRequest.filename)

                // Return PNG image with correct content type and filename
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    "attachment; filename=\"$filename.png\"",
                )
                call.respondBytes(imageBytes, ContentType.Image.PNG)
            } catch (e: Exception) {
                // Final catch-all for any unhandled exceptions
                application.log.error("Unhandled exception in /snap endpoint", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(
                        error = "Internal Server Error",
                        message = "An unexpected error occurred",
                        code = "UNHANDLED_ERROR",
                    ),
                )
            }
        }

        // Health check endpoint
        get("/health") {
            try {
                // Simple health check - test image generation with minimal code
                val testRequest =
                    SnapRequest(
                        code = "// test",
                        language = "kotlin",
                    )

                val testImage = generateCodeImageUseCase.execute(testRequest)

                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "status" to "healthy",
                        "timestamp" to java.time.Instant.now().toString(),
                        "imageGeneration" to "ok",
                    ),
                )
            } catch (e: Exception) {
                application.log.error("Health check failed", e)
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    ErrorResponse(
                        error = "Service Unhealthy",
                        message = "Health check failed: ${e.message}",
                        code = "HEALTH_CHECK_FAILED",
                    ),
                )
            }
        }
    }
}

/**
 * Generate a standardized filename with timestamp only when needed to avoid conflicts
 *
 * @param customFilename Optional custom filename provided by user
 * @return Filename without extension (will have .png added by caller)
 */
private fun generateFilename(customFilename: String?): String {
    return if (!customFilename.isNullOrBlank()) {
        // For custom filenames, use the original name first, then add timestamp if needed
        val sanitized = customFilename.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")

        // Check if this is a repeated request by using a simple in-memory cache
        // In a real production environment, you might want to check actual file existence
        // or use a more sophisticated approach
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        "${sanitized}_$timestamp"
    } else {
        // For auto-generated filenames, always use timestamp for uniqueness
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        "codesnapped_image_$timestamp"
    }
}
