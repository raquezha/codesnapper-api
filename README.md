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
  "darkMode": true,
  "title": "My Code Example",
  "filename": "my_custom_name",
  "backgroundTheme": "chatgpt5",
  "width": 800,
  "height": 400,
  "fontSize": 18,
  "fontFamily": "JetBrains Mono",
  "backgroundColor": "#1e1e1e",
  "textColor": "#ffffff"
}
```

### Response
Returns beautiful PNG images with macOS-style windows, syntax highlighting, and proper filenames.

- **Content-Type**: `image/png`
- **Content-Disposition**: `attachment; filename="codesnapped_image_20250811_143022.png"`
- **Auto-sizing**: Automatically calculates optimal dimensions when width/height not specified
- **Custom filenames**: Use `filename` field for custom names, otherwise gets timestamp-based name
- **Background themes**: Choose from 8 predefined beautiful background themes

### Key Features

🎨 **Auto-sizing**: Leave out `width`/`height` for perfect content-based sizing
📝 **Custom titles**: Add `title` field to show text in the macOS-style title bar
📁 **Smart filenames**: Custom filename support with automatic timestamp fallback
🖼️ **macOS styling**: Professional window design with traffic lights and line numbers
🌈 **Syntax highlighting**: Full color syntax highlighting for 20+ languages
🎭 **Background themes**: 8 curated background themes for every aesthetic

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

## Supported Syntax Highlighting Themes

The following **syntax highlighting themes** are supported for code coloring (case-insensitive):

- darcula
- monokai
- notepad
- matrix
- pastel
- atom (also accepts atomone, atom_one)

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
- **monokai** - Monokai Pro - Slightly warmer dark background
- **pure_white** - Pure White Minimal - Clean, for screenshots or prints
- **github_dark** - GitHub Dark - Familiar GitHub dark mode colors

### Usage Example:
```json
{
  "backgroundTheme": "chatgpt5"
}
```

> 📝 **Future Enhancement**: Custom color background themes will be supported in a future update. For now, choose from the predefined themes above.

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

## 🚀 Quick Start & Testing

### Using the Enhanced Demo JSON

For faster testing and demonstrations, use the included `enhanced_demo.json` file:

```bash
# Generate the enhanced macOS demo image
curl -X POST http://localhost:8081/snap \
  -H "Content-Type: application/json" \
  -d @enhanced_demo.json \
  -o final_macos_enhanced.png

# Or with custom output filename
curl -X POST http://localhost:8081/snap \
  -H "Content-Type: application/json" \
  -d @enhanced_demo.json \
  -O -J
```

The `enhanced_demo.json` contains a complete Kotlin example showcasing:
- 🎨 Beautiful macOS-style window with title bar
- 🌈 Darcula syntax highlighting theme
- 🖼️ ChatGPT-5 gradient background theme
- 📐 High-resolution output (1200x800)
- 🔤 JetBrains Mono font styling

### Quick Curl Examples

```bash
# Simple Kotlin example
curl -X POST http://localhost:8081/snap \
  -H "Content-Type: application/json" \
  -d '{
    "code": "fun hello() {\n  println(\"Hello World!\")\n  return \"success\"\n}",
    "language": "kotlin",
    "filename": "hello_world",
    "backgroundTheme": "chatgpt5"
  }' \
  -o hello_world.png

# Python with different theme
curl -X POST http://localhost:8081/snap \
  -H "Content-Type: application/json" \
  -d '{
    "code": "def fibonacci(n):\n    if n <= 1:\n        return n\n    return fibonacci(n-1) + fibonacci(n-2)",
    "language": "python",
    "theme": "monokai",
    "backgroundTheme": "nord",
    "title": "Fibonacci Function"
  }' \
  -O -J

# JavaScript with auto-sizing
curl -X POST http://localhost:8081/snap \
  -H "Content-Type: application/json" \
  -d '{
    "code": "const greet = (name) => {\n  console.log(`Hello, ${name}!`);\n  return { message: \"success\", name };\n};",
    "language": "javascript",
    "theme": "darcula",
    "darkMode": true,
    "backgroundTheme": "github_dark"
  }' \
  -O -J
```

### Creating Custom JSON Files

Create your own JSON files for repeated testing:

```bash
# Create a custom demo
cat > my_demo.json << 'EOF'
{
  "code": "// Your custom code here\nclass MyClass {\n  fun myMethod() = \"Hello!\"\n}",
  "language": "kotlin",
  "theme": "darcula",
  "darkMode": true,
  "title": "My Custom Demo",
  "filename": "my_custom_demo",
  "backgroundTheme": "one_dark",
  "fontSize": 18
}
EOF

# Use your custom JSON
curl -X POST http://localhost:8081/snap \
  -H "Content-Type: application/json" \
  -d @my_demo.json \
  -o my_custom_demo.png
```

### 🚨 Troubleshooting

#### Binary Output Warning
If you see this error message:
```
Warning: Binary output can mess up your terminal. Use "--output -" to tell
Warning: curl to output it to your terminal anyway, or consider "--output
Warning: <FILE>" to save to a file.
* Failure writing output to destination
```

**This is normal!** It means the API is working and returning a PNG image. The warning appears because you forgot the `-o filename.png` flag.

**✅ Solution:**
```bash
# Always use -o to save the image to a file
curl -X POST http://localhost:8081/snap \
  -H "Content-Type: application/json" \
  -d @enhanced_demo.json \
  -o final_macos_enhanced.png
```

**❌ Don't do this (causes the warning):**
```bash
# Missing -o flag - tries to display binary PNG in terminal
curl -X POST http://localhost:8081/snap \
  -H "Content-Type: application/json" \
  -d @enhanced_demo.json
```

The service is generating images correctly - you just need to save them properly!
