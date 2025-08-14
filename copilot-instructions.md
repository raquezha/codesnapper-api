# Copilot Instructions for Code Snapper API

## Project Overview

Code Snapper API is a production-ready Kotlin backend service built with Ktor that converts code snippets into beautiful, shareable images. The project follows Clean Architecture principles with clear separation of concerns.

## Project Structure

### Root Directory Organization
```
codesnapper/
├── README.md                   # Main project documentation
├── CHANGELOG.md               # Version history and changes
├── copilot-instructions.md    # This file - development guidelines
├── Dockerfile                 # Container deployment configuration
├── build.gradle.kts           # Build configuration and dependencies
├── settings.gradle.kts        # Gradle project settings
├── gradle.properties          # Gradle build properties
├── gradlew & gradlew.bat      # Gradle wrapper scripts
├── .gitignore                 # Git ignore patterns
├── .editorconfig              # Code formatting configuration
├── .ktlint                    # Kotlin linting configuration
├── .commitlintrc.json         # Commit message linting
├── src/                       # Main source code
├── config/                    # Configuration files
│   └── detekt/
│       └── detekt.yml         # Static analysis configuration
├── docs/                      # Documentation and examples
│   ├── project/               # Project documentation
│   │   ├── AUTOMATION.md      # CI/CD and automation docs
│   │   └── CONTRIBUTING.md    # Development workflow guide
│   ├── development/           # Development research and debug files
│   ├── examples/              # API usage examples
│   └── testing/               # Test fixtures and compliance reports
├── scripts/                   # Executable scripts and utilities
│   ├── test-automation.sh     # Automated testing script
│   └── test-material-design.sh # Material Design compliance testing
├── gradle/                    # Gradle configuration
│   ├── libs.versions.toml     # Version catalog
│   └── wrapper/               # Gradle wrapper files
└── .github/                   # GitHub Actions and workflows
```

## Architecture & Patterns

### Clean Architecture Structure
```
src/main/kotlin/net/raquezha/codesnapper/
├── Application.kt              # Main entry point & module configuration
├── HTTP.kt                     # HTTP configuration (CORS, etc.)
├── Monitoring.kt               # Health checks & monitoring
├── Routing.kt                  # Route definitions & error handling
├── Serialization.kt            # JSON serialization setup
├── controller/                 # API request/response models
│   ├── SnapRequest.kt         # Input DTOs with validation
│   └── ErrorResponse.kt       # Standardized error responses
├── domain/                     # Core business logic (framework-agnostic)
│   ├── model/                 # Domain entities & value objects
│   └── service/               # Domain services & interfaces
├── usecase/                    # Application business logic
│   ├── GenerateCodeImageUseCase.kt
│   └── HighlightCodeUseCase.kt
���── infrastructure/             # External concerns & implementations
│   ├── HighlightsCodeHighlighterService.kt
│   ├── ImageRendererFactory.kt
│   ├── Java2DImageRenderer.kt
│   └── MaterialDesignImageRenderer.kt
├── di/                        # Dependency injection configuration
│   └── AppModule.kt           # Koin module definitions
└── util/                      # Shared utilities
```

### Key Architectural Principles
- **Clean Architecture**: Dependencies point inward (infrastructure → usecase → domain)
- **Dependency Injection**: Uses Koin for IoC container
- **Single Responsibility**: Each class has one clear purpose
- **Interface Segregation**: Small, focused interfaces
- **Factory Pattern**: ImageRendererFactory for renderer selection
- **Strategy Pattern**: Different image renderers (macOS/Material Design)

## Technology Stack

### Core Technologies
- **Framework**: Ktor (Netty engine)
- **Language**: Kotlin (JVM target, Java 17)
- **Dependency Injection**: Koin
- **Serialization**: kotlinx.serialization
- **Testing**: kotlin.test with Ktor test framework
- **Build**: Gradle with Kotlin DSL

### Code Quality Tools
- **Linting**: ktlint (formatting & style)
- **Static Analysis**: detekt (code smells & complexity)
- **Coverage**: JaCoCo (minimum 80% coverage)
- **CI/CD**: GitHub Actions

## Coding Conventions

### Naming Conventions
- **Files**: PascalCase (e.g., `GenerateCodeImageUseCase.kt`)
- **Classes**: PascalCase (e.g., `CodeHighlighterService`)
- **Functions**: camelCase (e.g., `generateImage()`)
- **Variables**: camelCase (e.g., `snapRequest`)
- **Constants**: SCREAMING_SNAKE_CASE (e.g., `DEFAULT_THEME`)
- **Packages**: lowercase with dots (e.g., `net.raquezha.codesnapper`)

### Code Style Guidelines
- **Indentation**: 4 spaces (enforced by ktlint)
- **Line Length**: 120 characters max
- **Imports**: Organize with wildcards for 5+ imports from same package
- **Documentation**: KDoc for public APIs, inline comments for complex logic
- **Nullability**: Explicit null handling, prefer safe calls (`?.`)

### File Organization
- **One public class per file** (private helpers allowed)
- **File name matches primary class name**
- **Package structure reflects architectural layers**
- **Test files mirror main source structure**

### Error Handling Patterns
```kotlin
// Standardized error responses with proper HTTP status codes
when (validationResult) {
    is ValidationResult.Invalid -> call.respond(
        HttpStatusCode.BadRequest,
        ErrorResponse(
            error = "Validation Failed",
            message = "Input validation failed",
            code = "VALIDATION_ERROR",
            details = validationResult.errors.map { it.toDto() }
        )
    )
}

// Exception handling with proper logging
try {
    // Business logic
} catch (e: SerializationException) {
    call.respond(HttpStatusCode.BadRequest, ErrorResponse(...))
} catch (e: IOException) {
    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(...))
}
```

## Data Models & Serialization

### Request/Response Patterns
- **API Models**: Located in `controller/` package with `@Serializable` annotation
- **Domain Models**: Pure Kotlin data classes in `domain/model/`
- **DTO Mapping**: Extension functions for model conversion (e.g., `toDto()`)

### Validation Strategy
- **Input Validation**: Centralized in `InputValidationService`
- **Validation Results**: Sealed class pattern (`ValidationResult.Valid`/`Invalid`)
- **Error Details**: Structured error objects with field-level details

## Testing Conventions

### Test Structure
- **Location**: `src/test/kotlin/` mirroring main structure
- **Naming**: `ClassNameTest.kt` for test classes
- **Test Method Naming**: Backtick syntax with descriptive names using spaces (e.g., `fun \`should generate valid PNG when given valid request\`()`)
- **Framework**: kotlin.test with Ktor test framework
- **Pattern**: Descriptive sentences that clearly explain the behavior being tested

### Test Categories
- **Unit Tests**: Individual class testing with mocks
- **Integration Tests**: Full application context (`testApplication`)
- **API Tests**: End-to-end endpoint testing

### Test Patterns
```kotlin
@Test
fun `should generate valid PNG when given valid request`() = testApplication {
    application { module() }
    val response = client.post("/snap") {
        contentType(ContentType.Application.Json)
        setBody(validSnapRequest)
    }
    assertEquals(HttpStatusCode.OK, response.status)
    assertTrue(response.contentType()?.match(ContentType.Image.PNG) == true)
}

@Test
fun `should reject request with invalid preset`() = testApplication {
    application { module() }
    val response = client.post("/snap") {
        contentType(ContentType.Application.Json)
        setBody(invalidPresetRequest)
    }
    assertEquals(HttpStatusCode.BadRequest, response.status)
}
```

### Test Naming Conventions
- **Positive Tests**: `should [expected behavior] when [condition]` (e.g., `should generate valid image when given valid input`)
- **Negative Tests**: `should [error behavior] when [invalid condition]` (e.g., `should reject request when preset is invalid`)
- **Feature Tests**: `should [feature behavior] for [specific case]` (e.g., `should produce larger images for presentation preset`)
- **Validation Tests**: `should validate [rule] and [expected result]` (e.g., `should validate input and return error details`)

## API Design Patterns

### RESTful Conventions
- **Endpoints**: Descriptive nouns (`/snap` for image generation)
- **HTTP Methods**: POST for resource creation, GET for retrieval
- **Status Codes**: Proper HTTP semantics (200, 400, 500, etc.)
- **Content Types**: `application/json` for requests, `image/png` for responses

### Request/Response Structure
- **Consistent Naming**: camelCase for JSON fields
- **Optional Parameters**: Nullable with sensible defaults
- **Backward Compatibility**: Deprecated fields maintained with warnings
- **Extensibility**: Preset system for common configurations

## Configuration & Environment

### Application Configuration
- **Ktor Config**: `application.conf` for deployment settings
- **Build Config**: `build.gradle.kts` with version catalogs (`libs.versions.toml`)
- **Environment Variables**: For secrets and deployment-specific values

### Preset System
- **Default Presets**: `"default"`, `"light"`, `"presentation"`, `"compact"`
- **Design Systems**: `"macos"` (default) and `"material"`
- **Theme Support**: Various background themes with validation

## Development Workflow

### Git Conventions
- **Branching**: Git Flow (feature/, fix/, docs/, refactor/)
- **Commits**: Conventional Commits (feat:, fix:, docs:, test:, refactor:)
- **PR Workflow**: Required code review, all CI checks must pass

### Quality Gates
- **Pre-commit**: ktlint formatting
- **CI Pipeline**: Tests, linting, static analysis, coverage
- **Coverage Threshold**: Minimum 80% test coverage
- **Code Review**: Required before merge

### Documentation Requirements
- **Code Documentation**: KDoc for public APIs
- **Architecture Documentation**: Update CONTEXT.md for architectural changes
- **Change Documentation**: Update CHANGELOG.md for all user-facing changes
- **API Documentation**: Maintain README.md examples

## Common Patterns & Best Practices

### Dependency Injection
```kotlin
// Service registration in AppModule.kt
val appModule = module {
    single<CodeHighlighterService> { HighlightsCodeHighlighterService() }
    single { GenerateCodeImageUseCase(get(), get()) }
}

// Usage in application code
class SomeController {
    private val useCase by inject<GenerateCodeImageUseCase>()
}
```

### Use Case Pattern
```kotlin
class GenerateCodeImageUseCase(
    private val highlighterService: CodeHighlighterService,
    private val rendererFactory: ImageRendererFactory
) {
    suspend fun execute(request: GenerateImageRequest): ByteArray {
        // Business logic here
    }
}
```

### Factory Pattern
```kotlin
class ImageRendererFactory {
    fun createRenderer(designSystem: String): ImageRenderer = when (designSystem) {
        "material" -> MaterialDesignImageRenderer()
        "macos" -> Java2DImageRenderer()
        else -> Java2DImageRenderer() // default
    }
}
```

## Performance Considerations

- **Image Generation**: Async processing with coroutines
- **Memory Management**: ByteArray streaming for large images
- **Caching**: Consider caching for repeated requests (future enhancement)
- **Resource Management**: Proper disposal of graphics contexts

## Security Considerations

- **Input Validation**: All inputs validated before processing
- **Error Information**: No sensitive data in error responses
- **CORS Configuration**: Properly configured for web clients
- **Content Type Validation**: Strict content type checking

## Future Extensibility

- **Plugin Architecture**: Ready for additional image renderers
- **Preset System**: Easy addition of new presets
- **Theme Support**: Extensible theme system
- **Output Formats**: Architecture supports multiple output formats

## 🚨 CRITICAL COMMIT RULE - NEVER FORGET!

**ALWAYS USE SINGLE TERMINAL COMMAND FOR COMMIT MESSAGES**

❌ NEVER break commit messages across multiple terminal lines like this:
```
git commit -m "feat: add enterprise security
- Added rate limiting
- Added security headers"
```

✅ ALWAYS use single terminal command (multi-line content is OK with proper formatting):
```bash
# Single line commit message (preferred for simplicity)
git commit -m "feat: add enterprise security and performance infrastructure"

# Multi-line commit message in single terminal command using \n escape sequences
git commit -m "docs: update commit rule in copilot instructions\n- Clarified that multi-line commit messages are allowed\n- Must be formatted as single terminal command (not broken across lines)\n- Added examples of both single-line and multi-line formats\n- Emphasized terminal command execution safety"
```

**WHY:** Breaking commit commands across multiple terminal lines with unescaped quotes breaks terminal command execution. This has caused failed commits that appeared successful but weren't actually pushed.

**THE CORRECT WAY:** Use `\n` escape sequences within a single terminal command to create multi-line commit messages.

**ENFORCEMENT:** Every git commit must be a single terminal command to ensure proper execution and successful commits.

## Additional Guidelines

- Always validate commit success by checking the actual git output
- Use descriptive commit messages (single-line or multi-line both OK)
- Follow conventional commit format: `type: description`
- Common types: feat, fix, docs, style, refactor, test, chore
- For multi-line commits, use proper body formatting with blank line after summary

## Copilot Pro Usage & Etiquette
- Write clear, concise prompts for Copilot to maximize suggestion quality.
- Always review Copilot-generated code for correctness, security, and style.
- Use Copilot for incremental changes; refine output manually as needed.
- Provide feedback to Copilot by editing suggestions and re-prompting.
- Trust but verify: never blindly accept Copilot code, especially for critical logic or security-sensitive areas.

## Onboarding Checklist for New Contributors
- Read copilot-instructions.md, README.md, and CONTRIBUTING.md.
- Set up your IDE with recommended plugins (Kotlin, Ktor, Koin, Copilot).
- Run tests and automation scripts to verify your environment.
- Review CI/CD workflow files in .github/workflows/.
- Familiarize yourself with code style, commit, and PR conventions.

## Code Review Standards
- Check for logic errors, code style, and architectural consistency.
- Ensure all code (including Copilot-generated) is readable, maintainable, and tested.
- Validate commit messages against .commitlintrc.json and project rules.
- Review for security issues, proper error handling, and input validation.
- Require at least one approving review before merging PRs.

## Security Standards
- Follow OWASP Top 10 guidelines for web application security.
- Never commit secrets or sensitive data; use environment variables.
- Regularly update dependencies and review for vulnerabilities.
- Validate all user input and sanitize outputs.
- Use secure defaults for CORS, headers, and authentication.

## CI/CD Best Practices
- All code must pass linting, static analysis, and tests before merge.
- Use conventional commit messages for automated changelog generation.
- Automate releases and Docker builds via GitHub Actions.
- Document rollback procedures and release notes in CHANGELOG.md.

## Key File References
- **README.md**: Project overview, setup, and API usage.
- **CHANGELOG.md**: User-facing changes and release history.
- **CONTRIBUTING.md**: Development workflow and PR process.
- **SECURITY.md**: Security practices and disclosure policy.
- **copilot-instructions.md**: Coding, commit, and workflow standards.
- **.commitlintrc.json**: Commit message enforcement.
- **.editorconfig, .ktlint, detekt.yml**: Code style and static analysis.
- **.github/workflows/**: CI/CD automation.
- **scripts/**: Automation scripts for testing and compliance.
- **docs/testing/**: Test data and compliance reports.

## Developer Experience Tips
- Use automation scripts in scripts/ to streamline testing and compliance.
- Configure your IDE for Kotlin, Ktor, and Copilot integration.
- Use GitHub Codespaces or Docker for consistent dev environments.
- Troubleshoot common issues by checking logs, CI output, and documentation.

## Documentation Update Workflow
- Update copilot-instructions.md and other docs for any new conventions or major changes.
- Review documentation changes in PRs; require approval before merging.
- Keep documentation concise, actionable, and up-to-date.
