# 🖼️ Code Snapper — TODOs

## Core Features

- [ ] Implement POST /snap endpoint in Ktor
- [ ] Parse JSON payload: code, language, theme
- [ ] Integrate a syntax highlighter (library or headless browser)
- [ ] Support multiple languages and themes
- [ ] Render highlighted code as PNG (or SVG)
- [ ] Return image with correct Content-Type
- [ ] Validate input and handle errors gracefully
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
