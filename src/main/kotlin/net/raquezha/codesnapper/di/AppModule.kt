package net.raquezha.codesnapper.di

import net.raquezha.codesnapper.domain.service.CodeHighlighterService
import net.raquezha.codesnapper.domain.service.InputValidationService
import net.raquezha.codesnapper.infrastructure.HighlightsCodeHighlighterService
import net.raquezha.codesnapper.infrastructure.ImageRendererFactory
import net.raquezha.codesnapper.usecase.GenerateCodeImageUseCase
import net.raquezha.codesnapper.usecase.HighlightCodeUseCase
import org.koin.dsl.module

/**
 * AppModule
 *
 * Koin dependency injection configuration for Code Snapper.
 * Defines how services and use cases are instantiated and wired together.
 * Now supports both macOS and Material Design renderers via ImageRendererFactory.
 */
val appModule =
    module {
        // Services - Infrastructure implementations
        single<CodeHighlighterService> { HighlightsCodeHighlighterService() }
        single { ImageRendererFactory() }
        single { InputValidationService() }

        // Use Cases - Application layer
        single { HighlightCodeUseCase(get()) }
        single { GenerateCodeImageUseCase(get(), get()) }
    }
