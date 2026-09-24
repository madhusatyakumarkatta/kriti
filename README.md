# Krithi 🎵

**Krithi** is a modern, high-performance Android music player built with the latest Android development standards, offering a rich and intuitive user experience. 

---

## 🎯 Product Requirements Document (PRD)

### 1. Objective
To develop a robust, offline-first Android music player that provides seamless audio playback, elegant UI/UX through Jetpack Compose, and efficient media management using clean architecture principles.

### 2. Target Audience
* **Audiophiles & Music Lovers:** Users who need a fast, responsive app to manage and play their local audio libraries.
* **Android Enthusiasts:** Users who appreciate modern Material 3 design, dark mode, and fluid animations.

### 3. Key Features
* **Modern UI:** Built entirely with Jetpack Compose and Material 3 design principles.
* **Audio Playback Engine:** Powered by AndroidX Media3 (ExoPlayer) for stable background playback and media session support.
* **Library Management:** Scans local storage for media; organizes by Artists, Albums, and Tracks.
* **Playlists:** Custom playlist creation and management backed by a local Room database.
* **Dynamic Media Controls:** Notifications and lock screen controls integrating with the system MediaSession.
* **Image Loading:** Efficient album art fetching and caching using Coil.

### 4. Non-Functional Requirements
* **Architecture:** Strictly adheres to MVVM (Model-View-ViewModel) and Clean Architecture (Data, Domain, UI layers).
* **Performance:** Smooth 60fps scrolling in Compose; background processing using Kotlin Coroutines and Flow.
* **Compatibility:** Targets Android 14 (API 34) with backward compatibility down to Android 8.0 (API 26).

---

## 🚀 Phases of the Project

### Phase 1: Foundation & UI Architecture
* **Project Setup:** Gradle configuration, Hilt dependency injection setup, and package structuring (`data`, `di`, `domain`, `playback`, `ui`).
* **Theming & Design System:** Implementing Material 3 color palettes, typography, and basic layouts.
* **Component Library:** Building reusable Compose components (e.g., `AlbumCard`, `TrackListRow`).
* **Navigation:** Setting up `navigation-compose` for routing between Library, Player, and Playlist screens.

### Phase 2: Data Layer & Media Scanning
* **Domain Models:** Defining core entities (`Artist`, `Album`, `Track`, `PlaylistEntity`).
* **Local Storage (Room):** Setting up the Room database for user-generated data (Playlists, Favorites, Play History).
* **MediaStore Integration:** Querying the Android device's local storage to fetch audio files and metadata securely.
* **Repositories:** Creating data repositories and mapping them to domain use cases.

### Phase 3: Playback Engine (Media3)
* **Media Service:** Implementing a foreground `MediaSessionService` using AndroidX Media3.
* **Player Controller:** Connecting the ExoPlayer instance to the Compose UI state.
* **System Integration:** Handling audio focus, headset unplug events, and notification playback controls.
* **State Management:** Exposing current playback state and progress via Kotlin StateFlow to the UI.

### Phase 4: Polish, Animations & Advanced Features
* **UI Polish:** Adding fluid transitions, shared element transitions (if supported), and micro-animations for play/pause interactions.
* **Album Art Extraction:** Fetching embedded ID3 tag images and displaying them efficiently with Coil.
* **Advanced Audio:** (Optional) Equalizer integration and gapless playback.
* **Testing & Profiling:** Unit testing ViewModels and Use Cases; UI testing Compose screens.

---

## 🏗️ System Architecture

The app follows **Clean Architecture** combined with the **MVVM** pattern:

* **`ui` (Presentation Layer):** Contains Jetpack Compose screens, ViewModels, and state management. Observes `Flow` from the Domain layer.
* **`domain` (Domain Layer):** Contains core business models (`Artist.kt`, `Track.kt`) and Use Cases. Completely independent of Android framework classes.
* **`data` (Data Layer):** Handles data retrieval from the local `Room` database (`PlaylistEntity.kt`) and the Android `MediaStore`. Implements repository interfaces defined in the Domain layer.
* **`playback` (Media Layer):** Manages the `Media3` ExoPlayer lifecycle, MediaSession, and background service playback logic.
* **`di` (Dependency Injection):** Configures Dagger Hilt modules to provide singletons and scoped dependencies across the app.

---

## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Kotlin 1.9+ |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM, Clean Architecture |
| **Dependency Injection**| Dagger Hilt |
| **Asynchrony** | Coroutines & Flow |
| **Media Playback** | AndroidX Media3 (ExoPlayer) |
| **Local Database** | Room |
| **Image Loading** | Coil |
| **Navigation** | Navigation Compose |

---

## ⚙️ Quick Start

### Prerequisites
* Android Studio Iguana (or newer)
* JDK 17

### Build & Run
1. Clone the repository and open the `kriti` folder in Android Studio.
2. Allow Gradle to sync dependencies.
3. Build the project to generate Hilt and Room boilerplate classes.
4. Run the app on an emulator or physical device (API 26+).
