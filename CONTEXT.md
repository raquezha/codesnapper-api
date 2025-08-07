# 🖼️ Code Snapper — Project Context

## 🚀 Purpose
Code Snapper is a web-based Kotlin app that converts code input into beautiful, shareable images with syntax highlighting. It also exposes an API so other tools or services can use it.

## 🧰 Tech Stack
- Language: Kotlin
- Framework: Ktor (Web + REST API)
- Build Tool: Gradle (Kotlin DSL)
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

> The Highlights library is now integrated in the /snap endpoint. Incoming code, language, theme, and darkMode are parsed and highlighted using Highlights, with support for all available themes and languages. The endpoint currently returns highlight tokens as plain text for testing. Next steps: render as HTML or image.

- [x] Fix: HTML output for syntax highlighting now correctly matches code structure and order (no duplicate or misplaced tokens). The highlightsToHtml function sorts highlights and processes each region only once.
- [ ] Support multiple languages and themes
- [ ] Render highlighted code as PNG (or SVG)
- [ ] Return image with correct Content-Type
- [ ] Validate input and handle errors gracefully
- [ ] Add unit and integration tests for the endpoint
- [ ] Add download/preview URL
- [ ] Implement rate limiting or usage tracking
- [ ] Build a frontend playground
- [ ] Add pastebin-like history

## 🌱 Future Features (Nice to have)
- Download link or preview URL
- Rate limiting or usage tracking
- Frontend playground (optional)
- Pastebin-like history (temporary or persistent)

## 🤖 AI Notes
Use this file as context when writing, refactoring, or generating features. Always keep the API shape consistent and make image output the core priority.
