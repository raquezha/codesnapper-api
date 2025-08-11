# 🖼️ Code Snapper

A web-based Kotlin service that converts code snippets into beautiful, syntax-highlighted images with **dual design system support**. Built with Ktor and following Clean Architecture principles with Koin dependency injection.

## 🎨 Design Systems

Choose between two professionally designed aesthetics:

- **macOS Style** (`"designSystem": "macos"`) - Traffic light controls, 14px radius, authentic macOS window chrome
- **Material Design** (`"designSystem": "material"`) - Material Design 3 compliant with 100/100 compliance score ✅

## 🤖 Built with AI Assistance

This project was developed with **GitHub Copilot Pro** as a coding accelerator. While I have years of experience in software development, I used AI to fast-track the implementation and maintain high code quality. The AI helped with:

- Rapid prototyping and boilerplate generation
- Clean Architecture pattern implementation
- Dependency injection setup with Koin
- Material Design 3 compliance framework
- Comprehensive testing automation

The core architectural decisions, problem-solving approach, and project direction remain human-driven. AI simply accelerated the development process! 🚀

## 🚀 Current Status

✅ **Production Ready**: Complete PNG image rendering with syntax highlighting
✅ **Dual Design Systems**: macOS and Material Design 3 support
✅ **100% Material Design 3 Compliance**: Verified by automated testing framework
✅ **Clean Architecture**: Full implementation with Koin DI
✅ **Comprehensive Testing**: Automated Material Design compliance validation
✅ **Java 17 Compatible**: Optimized for production environments

## 🧰 Tech Stack

- **Language**: Kotlin (Java 17 compatibility)
- **Framework**: Ktor (Web + REST API)
- **Build Tool**: Gradle (Kotlin DSL)
- **Dependency Injection**: Koin 3.5.6
- **Syntax Highlighting**: Highlights JVM 1.0.0
- **JSON Processing**: Gson 2.10.1 (testing framework)
- **Output**: PNG image rendering with Java2D
- **Design Systems**: macOS + Material Design 3 compliant renderers
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
  "title": "My Code Example",
  "filename": "my_custom_name",
  "backgroundTheme": "chatgpt5",
  "designSystem": "material",
  "width": 800,
  "height": 400,
  "fontSize": 18,
  "fontFamily": "JetBrains Mono"
}
```

### Response
Returns beautiful PNG images with your chosen design system, syntax highlighting, and proper filenames.

- **Content-Type**: `image/png`
- **Content-Disposition**: `attachment; filename="codesnapped_image_20250812_143022.png"`
- **Auto-sizing**: Automatically calculates optimal dimensions when width/height not specified
- **Custom filenames**: Use `filename` field for custom names, otherwise gets timestamp-based name
- **Background themes**: Choose from 8 predefined beautiful background themes
- **Design Systems**: Choose between macOS or Material Design aesthetics

### Key Features

🎨 **Dual Design Systems**: macOS or Material Design 3 aesthetics
📐 **Auto-sizing**: Perfect content-based sizing with bulletproof text measurement
📝 **Custom titles**: Add `title` field to show text in the window title bar
📁 **Smart filenames**: Custom filename support with automatic timestamp fallback
🖼️ **Professional styling**: Window designs with proper controls and line numbers
🌈 **Syntax highlighting**: Full color syntax highlighting for 20+ languages
🎭 **Background themes**: 8 curated background themes for every aesthetic
♿ **Accessibility**: WCAG AA compliant contrast ratios in Material Design mode

## ✅ Completed Features

- [x] POST /snap endpoint with complete PNG image rendering
- [x] JSON payload parsing with comprehensive validation
- [x] Highlights library integration with real syntax highlighting
- [x] Multiple language and theme support with validation
- [x] Clean Architecture implementation (domain/usecase/infrastructure layers)
- [x] Koin dependency injection integration
- [x] **Dual design system support** (macOS + Material Design)
- [x] **Material Design 3 compliance** (100/100 score verified)
- [x] **Automated testing framework** for Material Design compliance
- [x] **Java 17 compatibility** and build optimization
- [x] Input validation and comprehensive error handling
- [x] **Version catalog dependency management**
- [x] **Code quality tools** (ktlint, detekt, JaCoCo)

## 🧪 Testing & Quality Assurance

### Material Design Compliance Testing
Run automated Material Design 3 compliance validation:
```bash
./test-material-design.sh
```

**Current Status: 🏆 100/100 Perfect Compliance**

### Automated Testing
Run comprehensive design system tests:
```bash
./test-automation.sh
```

### Code Quality
```bash
./gradlew codeQuality        # Run all quality checks
./gradlew ktlintFormat       # Auto-fix code formatting
./gradlew runMaterialDesignTests  # Material Design validation
```

## 🎯 Next Priorities

- [ ] Add unit and integration tests for endpoints
- [ ] Add download/preview URL functionality
- [ ] Implement rate limiting or usage tracking
- [ ] Build a frontend playground
- [ ] Add pastebin-like history

## Supported Languages

The following languages are supported (case-insensitive):

- kotlin, java, swift, c, cpp, python
- typescript, javascript, go, rust, ruby, php
- scala, groovy, objectivec, dart, shell, sql
- html, css, json, yaml, xml, markdown

## Supported Syntax Highlighting Themes

The following **syntax highlighting themes** are supported for code coloring (case-insensitive):

- **darcula** - IntelliJ IDEA dark theme
- **monokai** - Classic Sublime Text theme
- **notepad** - Clean minimal theme
- **matrix** - Retro green-on-black theme
- **pastel** - Soft pastel colors
- **atom** - Atom editor theme (also accepts atomone, atom_one)

Each theme supports both dark and light mode via the `darkMode` boolean in the request.

> 💡 **Note**: These themes control the **syntax highlighting colors** of your code (keywords, strings, etc.), not the background. For background styling, see the Background Themes section below.

## 🎭 Background Themes

Choose from 8 carefully curated **background themes** for your code images:

### Available Themes (case-insensitive):

- **darcula** - IntelliJ Darcula (default) - Dark neutral grays, easy on the eyes
- **chatgpt5** - ChatGPT-5 Gradient - Soft blue-green blend, modern and calm
- **solarized_dark** - Solarized Dark - Classic dev theme with warm accents
- **nord** - Nord - Cool Arctic blues and grays
- **one_dark** - One Dark Pro - VS Code-inspired balanced dark tone
- **cyberpunk** - Neon Cyberpunk - Electric purple and pink gradients
- **sunset** - Warm Sunset - Orange to pink gradient, cozy and inviting
- **ocean** - Deep Ocean - Blue gradient depths, calming and professional

## 🎨 Design System Details

### macOS Style (`"designSystem": "macos"`)
- Traffic light window controls (red, yellow, green circles)
- 14px border radius for authentic macOS feel
- Semi-transparent title bar with subtle gradients
- macOS-style shadows and window chrome

### Material Design (`"designSystem": "material"`)
- Material action buttons (close, minimize, fullscreen icons)
- 12px border radius (Material Design 3 specification)
- Material elevation shadows (2dp, 4dp, 8dp levels)
- Material Design 3 color tokens and typography
- 8dp grid system alignment
- Roboto font preference
- **WCAG AA accessibility compliance** with proper contrast ratios
- **100% Material Design 3 compliance** verified by automated testing

## 🏛️ Architecture

This project follows **Clean Architecture** principles:

- **domain/model/**: Core business models (CodeSnippet, HighlightedCode, ImageConfiguration)
- **domain/service/**: Business logic interfaces (CodeHighlighterService, ImageRenderingService)
- **usecase/**: Application use cases (HighlightCodeUseCase, GenerateCodeImageUseCase)
- **infrastructure/**: Implementation details (HighlightsCodeHighlighterService, MaterialDesignImageRenderer, Java2DImageRenderer)
- **controller/**: HTTP layer and DTOs (SnapRequest)
- **di/**: Dependency injection with Koin

### Design System Architecture
- `ImageRendererFactory` - Factory pattern for renderer selection
- `Java2DImageRenderer` - macOS-style rendering
- `MaterialDesignImageRenderer` - Material Design 3 compliant rendering (100% compliance verified)
- Both implement `ImageRenderingService` interface for seamless swapping

## 🛠️ Development

### Running the Server
```bash
./gradlew run
```

### Code Quality & Testing
```bash
# Format code
./gradlew ktlintFormat

# Run static analysis
./gradlew detekt

# Generate code coverage report
./gradlew jacocoTestReport

# Run all quality checks
./gradlew codeQuality

# Test Material Design compliance
./test-material-design.sh

# Run comprehensive testing
./test-automation.sh
```

## 📊 Compliance & Quality

- **Material Design 3**: 🏆 100/100 Perfect Compliance
- **WCAG AA Accessibility**: ✅ All contrast ratios ≥4.5:1
- **Typography Standards**: ✅ Material Design type scale
- **Grid System**: ✅ 8dp grid alignment
- **Code Quality**: ✅ ktlint, detekt, JaCoCo configured
- **Java Compatibility**: ✅ Java 17 optimized
