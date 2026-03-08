# NegoPlayer 🎬

> **The Ultimate Android Media Platform**  
> 🇵🇰 **Proudly Built in Pakistan**

---

## About

**NegoPlayer** is a production-grade, feature-rich Android media player built with the latest Android technologies. Designed to rival premium media applications, it supports local playback, streaming, IPTV, advanced subtitle systems, and much more.

**Author:** Muhammad Umar Jabbar  
**Location:** Rawalpindi, Punjab, Pakistan 🇵🇰  
**Version:** 1.0.0

---

## Features

### 🎬 Playback
- 4K & HDR video playback
- Hardware-accelerated decoding via Media3/ExoPlayer
- Dolby Audio compatibility
- Adaptive bitrate streaming (HLS, DASH)
- Playback speed control (0.25x – 2.0x)
- Frame-by-frame stepping

### 👆 Gesture Controls
- Swipe left/right to seek
- Swipe up/down (left half) for brightness
- Swipe up/down (right half) for volume
- Double-tap to seek ±10 seconds

### 📡 Streaming
- HTTP/HTTPS direct URL streaming
- HLS (M3U8) adaptive streaming
- MPEG-DASH streaming
- RTMP live streaming
- RTSP streaming

### 📺 IPTV
- M3U/M3U8 playlist support
- Channel grouping and search
- Live TV indicators
- Remote playlist loading

### 📚 Media Library
- Automatic MediaStore scanning
- Folder-based browsing
- Video thumbnail generation
- Advanced search and sorting
- Metadata extraction

### 📝 Subtitle Engine
- SRT format support
- ASS/SSA format support
- WebVTT format support
- Multiple subtitle tracks
- Subtitle synchronization

### 🖼️ Advanced Modes
- Picture-in-Picture (PiP)
- Background audio playback
- Landscape fullscreen mode
- Screen lock during playback

### 💾 Offline Features
- Playback history
- Resume from last position
- Bookmarks
- Watch Later list

---

## Technology Stack

| Technology | Version |
|-----------|---------|
| Kotlin | 2.0.0 |
| Jetpack Compose | 2024.06.00 BOM |
| Media3 / ExoPlayer | 1.4.0 |
| Material Design 3 | Latest |
| Hilt DI | 2.51.1 |
| Room Database | 2.6.1 |
| Kotlin Coroutines | 1.8.1 |
| Coil | 2.7.0 |
| Retrofit | 2.11.0 |
| Android Target SDK | 35 |

---

## Architecture

```
NegoPlayer/
├── app/                    # Main application module
├── core/                   # Shared utilities and models
├── data/                   # Room database, repositories
├── player/                 # ExoPlayer engine and service
├── network/                # API clients and networking
├── ui/                     # Shared UI components
├── analytics/              # Usage analytics
├── media-library/          # MediaStore scanner
├── subtitle-engine/        # Subtitle parsing and rendering
└── streaming/              # IPTV and streaming logic
```

**Architecture Pattern:** Clean Architecture + MVVM  
**UI Framework:** Jetpack Compose  
**Dependency Injection:** Hilt

---

## CI/CD

This project uses GitHub Actions for automated builds and Gemini AI-powered self-repair.

**Workflow:** `.github/workflows/gemini-repair.yml`

### Required Secrets:
| Secret | Description |
|--------|-------------|
| `GEMINI_API_KEY` | Google Gemini API key for auto-repair |
| `KEYSTORE_BASE64` | Base64-encoded release keystore (optional) |
| `KEY_ALIAS` | Keystore key alias (optional) |
| `KEY_PASSWORD` | Keystore key password (optional) |
| `STORE_PASSWORD` | Keystore store password (optional) |

### Repair Loop:
1. Attempt build
2. On failure → Gemini analyzes logs and repairs code
3. Retry build
4. Repeat up to 4 times until success
5. Upload APK artifact on success

---

## Building

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 35

### Local Build
```bash
git clone https://github.com/yourusername/NegoPlayer.git
cd NegoPlayer
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

---

## Requirements

- Android 8.0 (API 26) or higher
- Internet permission for streaming
- Storage permission for local media

---

## About The Developer

**Muhammad Umar Jabbar**  
Android Developer | Rawalpindi, Pakistan 🇵🇰

Built with passion from the heart of Pakistan. NegoPlayer represents the spirit of Pakistani innovation in technology.

*"Proudly Built in Pakistan — From Rawalpindi, to the World"*

---

## License

```
Copyright 2026 Muhammad Umar Jabbar

Licensed under the Apache License, Version 2.0
```

---

---

🇵🇰 **پاکستان زندہ باد** — Pakistan Zindabad

Made with ❤️ in Pakistan | Muhammad Umar Jabbar | Rawalpindi
