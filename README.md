# 🖼️ Code Snapper

A web-based Kotlin service that converts code snippets into beautiful, syntax-highlighted images. Built with Ktor and following Clean Architecture principles with Koin dependency injection.

## 🤖 Built with AI Assistance

This project was developed with **GitHub Copilot Pro** as a coding accelerator. While I have years of experience in software development, I used AI to fast-track the implementation and maintain high code quality. The AI helped with:

- Rapid prototyping and boilerplate generation
- Clean Architecture pattern implementation
- Dependency injection setup with Koin
- Documentation and conventional commits

The core architectural decisions, problem-solving approach, and project direction remain human-driven. AI simply accelerated the "vibe coding" process! 🚀

## 🚀 Current Status

✅ **Syntax Highlighting Complete**: The `/snap` endpoint fully works with real syntax highlighting using the Highlights library. It accepts code, language, theme, and darkMode parameters and returns colorized HTML.

✅ **Dependency Injection**: Koin DI is fully integrated with Clean Architecture for maintainable and testable code.

## 🧰 Tech Stack

- **Language**: Kotlin
- **Framework**: Ktor (Web + REST API)
- **Build Tool**: Gradle (Kotlin DSL)
- **Dependency Injection**: Koin 3.5.6
- **Syntax Highlighting**: Highlights JVM 1.0.0
- **Architecture**: Clean Architecture principles

## 📦 API

### Endpoint
`POST /snap`

### Request
```json
{
  "code": "fun hello() = println(\"Hello World!\")",
  "language": "kotlin",
  "theme": "darcula",
  "darkMode": true
}
```

### Response
Currently returns syntax-highlighted HTML (`text/html`).
**Next**: Will return images (`image/png` or `image/svg+xml`).

## ✅ Completed Features

- [x] POST /snap endpoint implementation
- [x] JSON payload parsing (code, language, theme, darkMode)
- [x] Highlights library integration with real syntax highlighting
- [x] Multiple language and theme support with validation
- [x] Clean Architecture implementation (domain/usecase/infrastructure layers)
- [x] Koin dependency injection integration
- [x] HTML output with proper color styling
- [x] Input validation and error handling for unsupported languages/themes

## 🎯 Next Priorities

- [ ] Render highlighted code as PNG (or SVG)
- [ ] Return image with correct Content-Type
- [ ] Add unit and integration tests for the endpoint

## Optional Enhancements

- [ ] Add download/preview URL
- [ ] Implement rate limiting or usage tracking
- [ ] Build a frontend playground
- [ ] Add pastebin-like history

## Supported Languages

The following languages are supported (case-insensitive):

- kotlin
- java
- swift
- c
- cpp
- python
- typescript
- javascript
- go
- rust
- ruby
- php
- scala
- groovy
- objectivec
- dart
- shell
- sql
- html
- css
- json
- yaml
- xml
- markdown

## Supported Themes

The following themes are supported (case-insensitive):

- darcula
- monokai
- notepad
- matrix
- pastel
- atom (also accepts atomone, atom_one)

Each theme supports both dark and light mode via the `darkMode` boolean in the request.

## 🧪 Code Quality

This project uses comprehensive code quality tools to maintain high standards:

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

**Tools included:**
- **ktlint 1.0.1**: Code formatting and style checking
- **detekt 1.23.4**: Static analysis for potential bugs and code quality issues
- **JaCoCo 0.8.11**: Code coverage reporting (compatible with Java 23)

All reports are generated in the `build/reports/` directory with multiple formats (HTML, XML, text).

## 🤖 Automation

This project includes professional automation following industry best practices:

### Pre-commit Hooks
Automatically runs before every commit:
- Code formatting with ktlint
- Static analysis with detekt
- Unit tests
- Auto-stages formatted files

### CI/CD Pipeline
GitHub Actions workflow runs on every PR:
- **Code Quality**: ktlint and detekt checks
- **Testing**: Unit tests with coverage reports
- **Build**: Application build verification
- **Artifacts**: Uploads reports and build artifacts

### Format on Save
IntelliJ IDEA configured for:
- Auto-format on save
- Optimize imports on the fly
- EditorConfig integration

### Future Automation (Roadmap)
- 🔄 **Dependency Updates**: Automated PRs for dependency updates
- 🛡️ **Security Scanning**: Vulnerability scanning for dependencies
- 🚀 **Release Automation**: Semantic versioning and automated releases
- 📚 **Documentation Generation**: Auto-generate API docs and coverage badges
- 📊 **Performance Monitoring**: Benchmark tests and memory usage tracking

---

See CONTEXT.md for project context and goals.
