# Code Snapper API

A production-ready Kotlin service that transforms code snippets into beautiful, syntax-highlighted images. Choose between macOS and Material Design aesthetics for your perfect visual style.

## Overview

Code Snapper API provides a simple REST endpoint that converts any code into professionally styled PNG images. Whether you need images for documentation, presentations, or automated workflows, our service delivers consistent, high-quality results.

### Design Systems

**macOS Style** - Authentic macOS window design with traffic light controls and native styling
**Material Design** - Google Material Design 3 compliant with verified accessibility standards

## Quick Start

### API Endpoint
```
POST /snap
```

### Basic Request
```json
{
  "code": "fun hello() = println(\"Hello World!\")",
  "language": "kotlin",
  "designSystem": "material",
  "title": "My Code Example"
}
```

### Response
Returns a high-quality PNG image with proper syntax highlighting and professional styling.

## Key Features

**Dual Design Systems** Choose between macOS or Material Design aesthetics
**Smart Auto-sizing** Automatically calculates optimal dimensions for your content
**Custom Titles** Add meaningful titles to your code windows
**Background Themes** Eight carefully curated background themes available
**Syntax Highlighting** Support for 20+ programming languages
**Accessibility Compliant** WCAG AA standards met in Material Design mode

## API Reference

### Request Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `code` | string | The code content to render |
| `language` | string | Programming language (kotlin, java, python, etc.) |
| `designSystem` | string | Design aesthetic: "macos" or "material" |
| `title` | string | Optional window title |
| `backgroundTheme` | string | Theme: darcula, chatgpt5, nord, etc. |
| `width` | number | Image width (auto-calculated if omitted) |
| `height` | number | Image height (auto-calculated if omitted) |
| `fontSize` | number | Font size in pixels |
| `fontFamily` | string | Font family name |
| `preset` | string | Optional: default | light | presentation | compact (applies base layout) |

### Supported Languages

**Popular Languages**: kotlin, java, swift, python, typescript, javascript, go, rust
**Web Technologies**: html, css, json, yaml, xml
**System Languages**: c, cpp, shell, sql
**Others**: ruby, php, scala, groovy, dart, markdown

### Background Themes

**darcula** Classic IntelliJ dark theme
**chatgpt5** Modern blue-green gradient
**solarized_dark** Developer favorite with warm accents
**nord** Cool Arctic blues and grays
**one_dark** VS Code inspired theme
**cyberpunk** Electric purple and pink gradients
**sunset** Warm orange to pink gradient
**ocean** Deep blue gradient depths

### Presets

Use a preset to quickly apply a base layout (you can still override width, height, colors, etc.).

| Preset | Description |
|--------|-------------|
| `default` | Standard dark configuration |
| `light` | Light background + dark text |
| `presentation` | 1920x1080, larger font (24px), extra padding |
| `compact` | 800x600, smaller font (14px), reduced padding |

Example:
```json
{
  "code": "println(\"Hello\")",
  "language": "kotlin",
  "preset": "presentation",
  "designSystem": "material"
}
```

## Development

### Running Locally
```bash
./gradlew run
```

The API will be available at `http://localhost:8081`

### Testing
```bash
# Run all tests
./test-automation.sh

# Check Material Design compliance
./test-material-design.sh

# Code quality checks
./gradlew codeQuality
```

## Production Deployment

Code Snapper API is packaged as a Docker container for easy, zero-ops deployment. Pull the official image or build your own:

```bash
# Pull latest release
docker pull ghcr.io/raquezha/codesnapper:latest

# Run locally
docker run -p 8081:8081 ghcr.io/raquezha/codesnapper:latest
```

Alternatively, download the JAR and run directly:

```bash
java -jar codesnapper-<version>.jar
```

## Future Enhancements

Planned improvements include:

- Unit and integration tests for all endpoints
- Download/preview URL functionality
- Rate limiting and usage tracking
- Interactive frontend playground
- Pastebin-style history for saved snippets
