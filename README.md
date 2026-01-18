🎬 CinemaVault - Offline-First Movie App
CinemaVault is a modern Android application built to demonstrate Offline-First Architecture using the SSOT (Single Source of Truth) pattern. It allows users to browse trending movies, search for titles, view details, and bookmark their favorites—all with seamless offline support.

🚀 Features
Offline-First Experience:

Trending & Now Playing feeds are cached locally using Room.

Bookmarked Movies are persisted permanently.

Automatic Sync: The app displays local data immediately while fetching fresh data in the background.

Smart Conflict Resolution: Updates movie details without overwriting user bookmarks.

Smart Search:

Type-Ahead Search: Implements debouncing (500ms) to minimize API usage.

Cache-on-Read: Viewing a movie from search results automatically caches it to the local database for future offline access.

Rich UI/UX:

Built entirely with Jetpack Compose.

Material 3 Design (Clean, Modern Interface).

Immersive Details: Backdrop images and clean typography.

Bonus Features:

Deep Linking: Supports opening specific movies via external links (movieapp://details/{id}).

Sharing: Share movie details with friends via system intent.

🛠 Tech Stack
Language: Kotlin

UI Toolkit: Jetpack Compose

Architecture: MVVM (Model-View-ViewModel) + Clean Architecture

Dependency Injection: Hilt

Network: Retrofit & OkHttp

Local Database: Room (SQLite)

Image Loading: Coil

Async: Coroutines & Flow

🏗 Architecture Overview
The app follows the Single Source of Truth (SSOT) principle. The UI primarily observes the database, ensuring that the user always sees consistent data regardless of network status.

UI Layer: Observes StateFlow from ViewModel.

ViewModel: Requests data from the Repository.

Repository:

Read: Returns Local Data (Room) immediately.

Fetch: Calls API (Remote) in the background.

Write: Saves API data into Room.

Database (Room): Emits the new data to the UI via Flow.

📦 Setup & Installation
Clone the Repository

Bash

git clone https://github.com/yourusername/CinemaVault.git
Get a TMDB API Key

Sign up at TheMovieDB.org.

Go to Settings > API to generate your key.

Configure API Key

Open com.example.cinemavault.core.common.Constants.kt.

Replace the placeholder with your actual key:

Kotlin

const val API_KEY = "YOUR_ACTUAL_API_KEY_HERE"
Run the App

Open in Android Studio.

Sync Gradle.

Run on Emulator or Physical Device.

🧪 Testing Deep Links
To test the deep link feature (opening a specific movie from a URL), run the following command in your terminal while the app is installed:

Bash

# Opens the details screen for "The Godfather" (ID: 238)
adb shell am start -W -a android.intent.action.VIEW -d "movieapp://details/238" com.example.cinemavault
📂 Project Structure
com.example.cinemavault
├── core
│   ├── common       # Constants, Resource Wrapper
│   └── di           # Hilt Modules (AppModule, RepoModule)
├── data
│   ├── local        # Room Database, DAO, Entities
│   ├── remote       # Retrofit API, DTOs
│   ├── mapper       # Mappers (Entity <-> Domain <-> DTO)
│   └── repository   # Repository Implementation (Logic)
├── domain
│   ├── model        # Clean Data Classes
│   └── repository   # Repository Interface
└── presentation
├── components   # Reusable UI (MovieItem)
├── home         # HomeScreen & ViewModel
├── search       # SearchScreen & ViewModel
├── details      # DetailScreen & ViewModel
├── bookmarks    # BookmarksScreen & ViewModel
└── theme        # Colors, Type, Theme
✅ Task Checklist
[x] API Integration: Connect to TMDB API.

[x] Local Storage: Store movies using Room.

[x] Offline Support: Load UI from local DB (SSOT).

[x] Architecture: MVVM + Repository Pattern.

[x] Bookmarks: Allow users to save movies locally.

[x] UI: Display Trending, Now Playing, and Search results.

[x] Bonus: Deep Linking support.

[x] Bonus: Share functionality.

📄 License
This project is open source.