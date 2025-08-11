package net.raquezha.codesnapper.di

import net.raquezha.codesnapper.domain.service.CodeHighlighterService
import net.raquezha.codesnapper.domain.service.ImageRenderingService
import net.raquezha.codesnapper.infrastructure.HighlightsCodeHighlighterService
import net.raquezha.codesnapper.infrastructure.Java2DImageRenderer
import net.raquezha.codesnapper.usecase.GenerateCodeImageUseCase
import net.raquezha.codesnapper.usecase.HighlightCodeUseCase
import org.koin.dsl.module

/**
 * AppModule
 *
 * Koin dependency injection configuration for Code Snapper.
 * Defines how services and use cases are instantiated and wired together.
 */
val appModule =
    module {
        // Services - Infrastructure implementations
        single<CodeHighlighterService> { HighlightsCodeHighlighterService() }
        single<ImageRenderingService> { Java2DImageRenderer() }

        // Use Cases - Application layer
        single { HighlightCodeUseCase(get()) }
        single { GenerateCodeImageUseCase(get(), get()) }
    }
