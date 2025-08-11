package net.raquezha.codesnapper.di

import net.raquezha.codesnapper.domain.service.CodeHighlighterService
import net.raquezha.codesnapper.infrastructure.HighlightsCodeHighlighterService
import net.raquezha.codesnapper.usecase.HighlightCodeUseCase
import org.koin.dsl.module

/**
 * Koin Dependency Injection Module
 *
 * Defines all the dependencies for the Clean Architecture layers:
 * - Infrastructure implementations (services)
 * - Use cases (application layer)
 * - Domain services (interfaces)
 *
 * This follows the dependency inversion principle where concrete implementations
 * are injected into interfaces, making the code testable and flexible.
 */
val appModule = module {

    // Infrastructure layer - Concrete implementations
    single<CodeHighlighterService> { HighlightsCodeHighlighterService() }

    // Application layer - Use cases
    single { HighlightCodeUseCase(get()) }
}
