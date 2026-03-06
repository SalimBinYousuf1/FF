# 🎵 VibePlayer

A modern, sleek Android music player built with Jetpack Compose, Material 3, and Media3 ExoPlayer.

[![Build APK](https://github.com/YOUR_USERNAME/VibePlayer/actions/workflows/build.yml/badge.svg)](https://github.com/YOUR_USERNAME/VibePlayer/actions/workflows/build.yml)

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎵 Local Playback | Plays all local audio files (MP3, FLAC, AAC, OGG, WAV) |
| 🎨 Material 3 UI | Dynamic color theming with dark/light mode |
| 🎛️ Full Player | Animated full-screen player with blurred album art background |
| 📚 Library | Songs, Albums, Artists, Playlists tabs |
| 🔍 Search | Real-time search across songs, artists, albums |
| ❤️ Favorites | Favorite songs for quick access |
| 🔁 Repeat & Shuffle | Per-song, all, or off repeat; shuffle queue |
| 📋 Playlists | Create, manage, and delete playlists |
| 🔔 Notifications | Media controls in notification shade |
| 🎧 Background Play | Continuous playback with foreground service |
| 📊 Play Count | Tracks most played songs |
| 🕐 Recently Played | Quick access to recently listened songs |
| ⚙️ Settings | Theme, dynamic color, library preferences |

---

## 🏗️ Tech Stack

| Library | Purpose |
|---------|---------|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Declarative UI |
| **Material 3** | Design system |
| **Media3 ExoPlayer** | Audio playback engine |
| **Media3 Session** | Background playback & notification controls |
| **Hilt** | Dependency injection |
| **Room** | Local database (songs, playlists) |
| **DataStore** | User preferences |
| **Coil** | Image loading (album art) |
| **Accompanist** | Permissions handling |
| **Coroutines + Flow** | Async operations |
| **Navigation Compose** | In-app navigation |

---

## 📁 Project Structure

```
app/src/main/java/com/vibeplayer/
├── data/
│   ├── local/           # Room DB, DAOs, MediaStore scanner, DataStore
│   ├── model/           # Song, Album, Artist, Playlist, PlayerState models
│   └── repository/      # MusicRepository (single source of truth)
├── di/                  # Hilt dependency injection modules
├── service/             # MusicPlaybackService (Media3 SessionService)
├── ui/
│   ├── components/      # Reusable Composables (SongItem, MiniPlayer, AlbumArt...)
│   ├── navigation/      # NavHost & Screen routes
│   ├── screens/
│   │   ├── home/        # Home screen with recommendations
│   │   ├── player/      # Full-screen player
│   │   ├── library/     # Songs/Albums/Artists/Playlists tabs
│   │   ├── search/      # Real-time search
│   │   └── settings/    # App settings
│   ├── Theme.kt         # Material 3 color schemes
│   └── Typography.kt    # Text styles
├── viewmodel/           # PlayerViewModel, LibraryViewModel, SearchViewModel
├── MainActivity.kt
└── VibePlayerApp.kt     # Hilt Application class
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 35
- JDK 17

### Setup
```bash
git clone https://github.com/YOUR_USERNAME/VibePlayer.git
cd VibePlayer
./gradlew assembleDebug
```

Install on device:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🤖 GitHub Actions (Auto APK Build)

Every push to `main` automatically:
1. ✅ Runs unit tests
2. 🏗️ Builds a Debug APK
3. 📤 Uploads as a downloadable artifact (retained 30 days)

To download: Go to **Actions** → select a workflow run → **Artifacts** section.

### Creating a Release
Push a version tag to trigger a GitHub Release with APK attached:
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## 📱 Minimum Requirements
- Android 8.0 (API 26) or higher
- Storage permission for reading local music files

---

## 📄 License
MIT License — see [LICENSE](LICENSE) for details.
