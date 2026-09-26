# 📻 Neo Radio — Fully Functional Android & Android TV Radio Application with Python/Go Backend

![Neo Radio Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-blue)
![Backend](https://img.shields.io/badge/Backend-FastAPI%20%2B%20Go%20Proxy-green)
![Android](https://img.shields.io/badge/Android-Kotlin%20%2B%20Jetpack%20Compose-orange)
![License](https://img.shields.io/badge/License-MIT-purple)

**Neo Radio** is a complete, production-ready live streaming radio solution for Android phones and Android TV devices, powered by a high-performance Python FastAPI + Go stream proxy backend caching 50,000+ radio stations from Radio-Browser API mirrors.

---

## 🚀 Key Features

### 📡 Backend (FastAPI + Go Proxy)
- **FastAPI Core**: Async REST API serving M3U playlists, stations list, category filters, and real-time search.
- **Radio-Browser Integration**: Automatically fetches and caches over 50,000 radio stations across global mirrors with fallback mechanism.
- **Go Stream Proxy**: High-performance stream proxy supporting 1,000+ concurrent audio streams, ICY metadata pass-through, graceful shutdown, and Prometheus metrics (`/metrics`).
- **Caching**: Flexible file-based JSON cache or Redis cache with configurable TTL (6 hours default).
- **Dockerized Architecture**: Pre-configured `docker-compose.yml` for instant zero-config deployment.

### 📱 Android & Android TV App (Kotlin + Compose)
- **Unified Codebase**: Dual UI tailored for both Android Smartphones and Android TV (Leanback / D-pad support).
- **Jetpack Compose & Material 3**: Sleek dark and light themes with modern UI components.
- **Media3 ExoPlayer**: Reliable playback with foreground service, notification controls, Bluetooth hardware media session, and automatic reconnect mechanism on network drops.
- **Multi-Language Support**: Full localization for Azerbaijani (AZ), English (EN), Turkish (TR), and Russian (RU).
- **Sleep Timer & Favorites**: Flexible sleep timer (15, 30, 60, 90 mins) and DataStore persistent favorites.
- **Dynamic Configuration**: Fully configurable via `local.properties` and in-app settings.

---

## 📂 Repository Structure

```
.
├── backend/
│   ├── app/
│   │   ├── main.py              # FastAPI application entrypoint
│   │   ├── config.py            # Pydantic Settings configuration
│   │   ├── routers/             # API routes (m3u, stations, search, health, stream)
│   │   ├── services/            # Radio-Browser client, Cache, M3U builder
│   │   └── models/              # Pydantic Station model
│   ├── go-proxy/
│   │   ├── main.go              # High-performance Go Audio Stream Proxy
│   │   ├── go.mod
│   │   └── Dockerfile
│   ├── .env.example
│   ├── requirements.txt
│   └── Dockerfile
├── app/                         # Android App module
│   ├── src/main/java/com/3bneoplay/radio/
│   │   ├── MainActivity.kt      # Phone Activity
│   │   ├── TvMainActivity.kt    # TV Activity
│   │   ├── data/                # API, DataStore, Models, Repository
│   │   ├── player/              # ExoPlayer wrapper, PlaybackService, MediaSession
│   │   ├── ui/                  # Compose UI (Phone, TV, Components, NavGraph, Theme)
│   │   ├── viewmodel/           # Home, Stations, Player, Search, Favorites, Settings
│   │   └── di/                  # Hilt DI Module
│   └── build.gradle.kts
├── .github/workflows/ci-cd.yml  # GitHub Actions CI/CD pipeline
├── docker-compose.yml           # Backend multi-container setup
├── local.properties.example     # Android local config
└── README.md
```

---

## 🛠️ Backend Deployment & Setup

### Option 1: Docker Compose (Recommended)

1. Clone the repository and navigate to the root directory.
2. Build and start the backend containers:
   ```bash
   docker-compose up --build -d
   ```
3. Check services health:
   - FastAPI API: `http://localhost:8000/health`
   - Go Proxy Metrics: `http://localhost:8001/metrics`
   - Interactive Swagger Docs: `http://localhost:8000/docs`

### Option 2: Manual Local Setup

#### Python API
```bash
cd backend
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

#### Go Stream Proxy
```bash
cd backend/go-proxy
go run main.go
```

---

## 🌐 FastAPI Endpoints Overview

- `GET /` - API Information
- `GET /health` - Health check & cache statistics
- `GET /stats` - Total count of stations, countries, genres, and languages
- `GET /m3u` - M3U playlist export (Query params: `country`, `genre`, `language`, `minbitrate`, `limit`, `raw`)
- `GET /stations` - JSON list of stations with pagination (`limit`, `offset`)
- `GET /stations/{uuid}` - Get detailed information for a single station
- `GET /countries` - List of countries ordered by station count
- `GET /genres` - List of popular genres/tags
- `GET /languages` - List of languages
- `GET /search?q={query}` - Real-time search across names, countries, and tags
- `GET /stream?url={stream_url}` - Redirects to Go Proxy for streaming
- `POST /admin/refresh` - Refresh cached station database (Requires `X-Admin-Token` header)

---

## 📱 Android App Setup & Build

### Environment Configuration
Copy `local.properties.example` to `local.properties` or set environment variables:
```properties
API_BASE_URL=https://3bneoplay65.xyz/radio/
PLAYER_BUFFER_MS=3000
PLAYER_NOTIFICATION_ENABLED=true
UI_THEME=dark
UI_LANGUAGE=az
```

### Build & Run via Gradle
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Assemble Release APK
./gradlew assembleRelease
```
The output APK will be generated at:
`app/build.gradle.kts` → `app/build/outputs/apk/release/app-release.apk`

---

## 🧪 Testing & CI/CD

- **Backend Tests**:
  ```bash
  cd backend
  pytest
  ```
- **Android Tests**:
  ```bash
  ./gradlew testDebugUnitTest
  ```
- **Automated Workflow**: `.github/workflows/ci-cd.yml` automatically runs backend pytest, verifies Go build, runs Android unit tests, and compiles the Release APK on pushes to `main`.

---

## 📄 License

This project is open source and released under the [MIT License](LICENSE).
