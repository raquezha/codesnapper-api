# 🖼️ Code Snapper — Project Context

## 🚀 Purpose
Code Snapper is a web-based Kotlin app that converts code input into beautiful, shareable images with syntax highlighting. It also exposes an API so other tools or services can use it.

## 🧰 Tech Stack
- Language: Kotlin
- Framework: Ktor (Web + REST API)
- Build Tool: Gradle (Kotlin DSL)
- Dependency Injection: Koin 3.5.6
- Syntax Highlighting: Highlights JVM 1.0.0
- Output: PNG (or SVG) image
- Optional Renderer: Java2D or headless Chromium

## 📦 API Overview

### Endpoint
`POST /snap`

### Request Payload (JSON)
```json
{
  "code": "fun hello() = println(\"Hello\")",
  "language": "kotlin",
  "theme": "dark"
}
```

### Response
Returns a syntax-highlighted image of the provided code.
Content-Type: `image/png` or `image/svg+xml`

## 📝 Goals
- Generate beautiful code images from raw text input
- Accept multiple languages and themes
- Make it API-first (frontend optional)
- Lightweight and fast

## ✅ TODOs
- [x] Implement POST /snap endpoint in Ktor
- [x] Parse JSON payload: code, language, theme
- [x] Integrate a syntax highlighter (library or headless browser)

> ✅ **COMPLETED**: The Highlights library is fully integrated in the /snap endpoint. Incoming code, language, theme, and darkMode are parsed and highlighted using the correct Highlights Builder API. The HighlightsCodeHighlighterService now produces real syntax highlight tokens (no longer a dummy implementation) and returns properly formatted HTML with color styling.

- [x] Fix: HTML output for syntax highlighting now correctly matches code structure and order (no duplicate or misplaced tokens). The highlightsToHtml function sorts highlights and processes each region only once.
- [x] Support multiple languages and themes

> ✅ **COMPLETED**: Supported languages and themes are documented in README.md. The /snap endpoint validates both fields and returns HTTP 400 for unsupported values, ensuring robust API behavior. All utility functions (parseSyntaxLanguage, parseSyntaxTheme) are now properly utilized.

- [x] Clean Architecture implementation completed

> ✅ **COMPLETED**: All layers are properly implemented - domain models (CodeSnippet, HighlightedCode), service interfaces (CodeHighlighterService), use cases (HighlightCodeUseCase), infrastructure (HighlightsCodeHighlighterService), and controllers (SnapRequest). The HighlightsCodeHighlighterService now uses the real Highlights library API instead of returning empty lists.

- [x] Dependency Injection with Koin

> ✅ **COMPLETED**: Koin DI is fully integrated with Clean Architecture. All dependencies are managed through the `appModule` in `/di/AppModule.kt`. The routing layer uses `inject<HighlightCodeUseCase>()` instead of manual instantiation, making the code more testable and maintainable. Koin is configured with SLF4J logging for better debugging.

- [x] Code Quality Tools (ktlint, detekt, JaCoCo)

> ✅ **COMPLETED**: Comprehensive code quality tools are fully configured and integrated. ktlint 1.0.1 handles code formatting, detekt 1.23.4 performs static analysis, and JaCoCo 0.8.11 provides code coverage reporting. All tools are properly configured for Java 23 compatibility and Ktor project standards. Available via `./gradlew codeQuality` task.

- [x] Pre-commit Hooks & CI/CD Automation

> ✅ **COMPLETED**: Pre-commit hooks automatically run code quality checks before commits. GitHub Actions CI/CD pipeline runs on every PR with code quality checks, tests, and build verification. Format-on-save is configured for IntelliJ IDEA. All automation follows industry best practices from top Kotlin projects.

- [ ] Render highlighted code as PNG (or SVG)
- [ ] Return image with correct Content-Type
- [ ] Validate input and handle errors gracefully
- [ ] Add unit and integration tests for the endpoint
- [ ] Add download/preview URL
- [ ] Implement rate limiting or usage tracking
- [ ] Build a frontend playground
- [ ] Add pastebin-like history

## 🔮 Future Automation Features
- [ ] Dependency Updates: Automated PRs for dependency updates
- [ ] Security Scanning: Vulnerability scanning for dependencies
- [ ] Release Automation: Semantic versioning and automated releases
- [ ] Documentation Generation: Auto-generate API docs and code coverage badges
- [ ] Performance Monitoring: Benchmark tests and memory usage tracking

## 🤖 AI Notes
Use this file as context when writing, refactoring, or generating features. Always keep the API shape consistent and make image output the core priority.

## 🏛️ Architecture

This project follows Clean Architecture principles for maintainability and testability:

- **domain/model/**: Core business models (e.g., CodeSnippet, HighlightedCode)
- **domain/service/**: Business logic interfaces (e.g., CodeHighlighterService)
- **usecase/**: Application-specific use cases (e.g., HighlightCodeUseCase)
- **infrastructure/**: Implementations of interfaces, integrations (e.g., HighlightsCodeHighlighterService)
- **adapter/controller/**: HTTP layer, DTOs (e.g., SnapRequest)
- **di/**: Dependency injection configuration (Koin modules)
- **Routing.kt**: Ktor routing/controllers, delegates to use cases

**Dependency Injection with Koin:**
All dependencies are managed through Koin DI container. The `appModule` defines singleton instances for services and use cases. Controllers use `inject<T>()` to receive dependencies, ensuring loose coupling and testability.

## 🧪 Code Quality

This project includes comprehensive code quality tools with the following Gradle tasks:

```bash
# Code formatting and style
./gradlew ktlintCheck      # Check code formatting
./gradlew ktlintFormat     # Auto-fix formatting issues

# Static code analysis
./gradlew detekt           # Run static analysis for bugs and code smells

# Code coverage
./gradlew jacocoTestReport # Generate code coverage reports

# Run all quality checks
./gradlew codeQuality      # Run ktlint, detekt, and JaCoCo together
```

**Tools configured:**
- **ktlint 1.0.1**: Code formatting and style checking
- **detekt 1.23.4**: Static analysis for potential bugs and code quality issues
- **JaCoCo 0.8.11**: Code coverage reporting (compatible with Java 23)

Reports are generated in `build/reports/` directory with HTML, XML, and text formats.

This separation ensures the core logic is independent of frameworks and easy to test, extend, or swap out. See the codebase for concrete examples.
