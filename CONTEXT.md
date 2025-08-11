# 🖼️ Code Snapper — Project Context

## 🚀 Purpose
Code Snapper is a web-based Kotlin app that converts code input into beautiful, shareable images with syntax highlighting. It supports both **macOS** and **Material Design** aesthetics, giving users choice over their preferred design system. It also exposes an API so other tools or services can use it.

## 🧰 Tech Stack
- Language: Kotlin
- Framework: Ktor (Web + REST API)
- Build Tool: Gradle (Kotlin DSL)
- Dependency Injection: Koin 3.5.6
- Syntax Highlighting: Highlights JVM 1.0.0
- Output: PNG image rendering
- Design Systems: macOS (Java2D) + Material Design 3 compliant renderers

## 📦 API Overview

### Endpoint
`POST /snap`

### Request Payload (JSON)
```json
{
  "code": "fun hello() = println(\"Hello\")",
  "language": "kotlin",
  "theme": "dark",
  "title": "My Code Example",
  "filename": "my_custom_name",
  "backgroundTheme": "chatgpt5",
  "designSystem": "material",
  "width": 800,
  "height": 400
}
```

### Response
Returns a syntax-highlighted image of the provided code with standardized filename.
- Content-Type: `image/png`
- Content-Disposition: `attachment; filename="codesnapped_image_20250811_143022.png"`
- Auto-sizing: If width/height not specified, automatically calculates optimal dimensions
- Filename: Uses custom filename if provided, otherwise generates `codesnapped_image_<timestamp>.png`
- Background themes: Choose from 8 predefined beautiful background themes (darcula, chatgpt5, nord, etc.)
- **Design Systems**: Choose between `"macos"` (default) or `"material"` for different visual aesthetics

### 🎨 Design System Options

**macOS Style (`"designSystem": "macos"`):**
- Traffic light window controls (red, yellow, green circles)
- 14px border radius
- macOS-style shadows and semi-transparent title bar
- Authentic macOS window chrome

**Material Design Style (`"designSystem": "material"`):**
- Material action buttons (close, minimize, fullscreen icons)
- 12px border radius (Material Design 3 specification)
- Material elevation shadows (2dp, 4dp, 8dp levels)
- Material Design 3 color tokens and typography
- 8dp grid system alignment
- Roboto font preference

## 📝 Goals
- Generate beautiful code images from raw text input
- Accept multiple languages and themes
- **Support both macOS and Material Design aesthetics**
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

- [x] Render highlighted code as PNG (or SVG)

> ✅ **COMPLETED**: Full PNG image rendering pipeline implemented using Java2D Graphics2D. The `/snap` endpoint now returns high-quality PNG images with proper syntax highlighting colors, configurable fonts, dimensions, and themes. Includes ImageConfiguration with preset configurations for different use cases (dark/light themes, presentation mode, compact mode). The GenerateCodeImageUseCase orchestrates the complete flow from code input to PNG output through Clean Architecture layers.

- [x] Return image with correct Content-Type

> ✅ **COMPLETED**: The `/snap` endpoint now returns PNG images with proper `Content-Type: image/png` header instead of HTML. The routing layer uses `call.respondBytes(imageBytes, ContentType.Image.PNG)` to ensure correct HTTP response format.

- [x] Validate input and handle errors gracefully

> ✅ **COMPLETED**: Comprehensive input validation and error handling implemented for production use. Created InputValidationService with validation for code content, languages, themes, and image configuration parameters. Added robust error handling throughout the API with proper HTTP status codes, meaningful error messages, and detailed field-level validation feedback. Includes health check endpoint and comprehensive logging for monitoring.

- [x] **Design System Choice Implementation**

> ✅ **COMPLETED**: Users can now choose between macOS and Material Design aesthetics via the `designSystem` parameter. Implemented `MaterialDesignImageRenderer` following Material Design 3 specifications, `ImageRendererFactory` for renderer selection, and complete validation for design system choices. Both renderers use bulletproof text width calculation with multiple measurement techniques and dynamic percentage-based padding for optimal layout.

- [x] **Test Automation & Project Organization**

> ✅ **COMPLETED**: Created comprehensive test automation script (`test-automation.sh`) that starts the server and runs all design system tests automatically. Organized project structure with `docs/` folder containing testing files, debug utilities, and examples. Test files now follow consistent naming convention (`*_macos.json`, `*_material.json`).

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

## 🧪 **Testing & Automation**

### Automated Testing
Run the comprehensive test suite:
```bash
./test-automation.sh
```

This script:
- Starts the Code Snapper server automatically
- Runs all design system tests (macOS + Material Design)
- Generates comparison images for all test cases
- Provides detailed pass/fail reporting
- Saves generated images to `docs/testing/generated/`

### Manual Testing
Test files are organized in `docs/testing/`:
- `test_5_lines_macos.json` / `test_5_lines_material.json` - Small code snippets
- `test_15_lines_macos.json` / `test_15_lines_material.json` - Medium code samples
- `test_30_lines_macos.json` / `test_30_lines_material.json` - Longer code examples

### Debug Utilities
Debug files in `docs/debug/` help troubleshoot text measurement and width calculation issues:
- `debug_text_measurement.kt` - Font metrics analysis and text width debugging
- `research_text_width_solutions.kt` - Production-grade text width calculation research
- `debug_measurement_failure.kt` - Specific measurement failure analysis
- `debug_width_calc.kt` - Width calculation verification utilities

These utilities were essential for solving the bulletproof text width calculation that prevents text cutoff issues across different design systems.

## 🤖 AI Notes
Use this file as context when writing, refactoring, or generating features. Always keep the API shape consistent and make image output the core priority. The project now supports both macOS and Material Design aesthetics - ensure new features work with both design systems.

## 🏛️ Architecture

This project follows Clean Architecture principles for maintainability and testability:

- **domain/model/**: Core business models (e.g., CodeSnippet, HighlightedCode, ImageConfiguration)
- **domain/service/**: Business logic interfaces (e.g., CodeHighlighterService, ImageRenderingService)
- **usecase/**: Application-specific use cases (e.g., HighlightCodeUseCase, GenerateCodeImageUseCase)
- **infrastructure/**: Implementations of interfaces, integrations (e.g., HighlightsCodeHighlighterService, Java2DImageRenderer, MaterialDesignImageRenderer, ImageRendererFactory)
- **controller/**: HTTP layer, DTOs (e.g., SnapRequest)
- **di/**: Dependency injection configuration (Koin modules)
- **Routing.kt**: Ktor routing/controllers, delegates to use cases

**Dependency Injection with Koin:**
All dependencies are managed through Koin DI container. The `appModule` defines singleton instances for services and use cases. Controllers use `inject<T>()` to receive dependencies, ensuring loose coupling and testability. The `ImageRendererFactory` creates appropriate renderers based on design system choice.

**Design System Architecture:**
- `ImageRendererFactory` - Factory pattern for renderer selection
- `Java2DImageRenderer` - macOS-style rendering with traffic lights and macOS aesthetics
- `MaterialDesignImageRenderer` - Material Design 3 compliant rendering with proper elevation, color tokens, and typography
- Both renderers implement the same `ImageRenderingService` interface for seamless swapping

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
