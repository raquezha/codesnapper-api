package net.raquezha.codesnapper.di

import net.raquezha.codesnapper.domain.service.CacheService
import net.raquezha.codesnapper.domain.service.CodeHighlighterService
import net.raquezha.codesnapper.domain.service.InputValidationService
import net.raquezha.codesnapper.domain.service.RateLimitingService
import net.raquezha.codesnapper.infrastructure.HighlightsCodeHighlighterService
import net.raquezha.codesnapper.infrastructure.ImageRendererFactory
import net.raquezha.codesnapper.infrastructure.InMemoryCacheService
import net.raquezha.codesnapper.infrastructure.KtorRateLimitingService
import net.raquezha.codesnapper.usecase.GenerateCodeImageUseCase
import net.raquezha.codesnapper.usecase.HighlightCodeUseCase
import org.koin.dsl.module

/**
 * AppModule
 *
 * Koin dependency injection configuration for Code Snapper.
 * Now includes security and performance services following Clean Architecture.
 */
val appModule =
    module {
        // Services - Infrastructure implementations
        single<CodeHighlighterService> { HighlightsCodeHighlighterService() }
        single { ImageRendererFactory() }
        single { InputValidationService() }

        // New infrastructure services following domain → infrastructure pattern
        single<RateLimitingService> { KtorRateLimitingService() }
        single<CacheService> { InMemoryCacheService() }

        // Use Cases - Application layer
        single { HighlightCodeUseCase(get()) }
        single { GenerateCodeImageUseCase(get(), get()) } // Keep original signature for now
    }
