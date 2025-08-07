# 🖼️ Code Snapper

Turn code into beautiful, shareable images — fast.  
Built with Kotlin + Ktor. Exposes a simple API. No fluff, just code snaps.

---

## ✨ What It Does

Code Snapper is a backend web service that:

- Accepts code through a REST API
- Generates a syntax-highlighted image
- Returns the image (PNG or SVG) directly

Use it to create code snippet posts, docs, or social media-ready visuals.

---

## 🚀 Tech Stack

- **Kotlin**
- **Ktor** for the web backend
- **Gradle (Kotlin DSL)**
- Image generation with **Java2D** or **headless Chromium** (TBD)

---

## 📦 API

### `POST /snap`

Send your code, get an image back.

#### Request JSON:
```json
{
  "code": "fun hello() = println(\"Hello\")",
  "language": "kotlin",
  "theme": "dark"
}
```

#### Response:
- Content-Type: `image/png`
- Binary image data

Use it in frontends, chatbots, blogs, or anywhere a pretty code image is useful.

---

## 🛠 How to Run (Local Dev)

```bash
./gradlew run
```

The server will start on `http://localhost:8080`.

Use any HTTP client to test:
```bash
curl -X POST http://localhost:8080/snap \
  -H "Content-Type: application/json" \
  -d '{"code":"fun main() = println(\"Hello\")","language":"kotlin","theme":"dark"}' \
  --output output.png
```

---

## 🌱 Roadmap

- [ ] Add theme options (light, dark, etc.)
- [ ] Support more languages
- [ ] Add frontend preview (optional)
- [ ] API key or rate limiting (for public deployment)

---

## 🤖 Dev Notes

See [`CONTEXT.md`](./CONTEXT.md) for AI context and development insights.

---

## 🧑‍💻 Author

Built by [raquezha](https://github.com/raquezha)  
Crafted with Kotlin and questionable amounts of caffeine ☕

---

## 📄 License

MIT — feel free to fork, improve, or snap away.
