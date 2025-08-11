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

---

See CONTEXT.md for project context and goals.
