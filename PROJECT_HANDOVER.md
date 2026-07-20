# Personal AI Life OS: Deep Dive Architectural Handover

This document is a deeply technical, component-level breakdown of the **Personal AI Life OS**. It is designed for engineers who want to understand the exact data flows, architectural decisions, and code-level implementations of the application. 

---

## 🏛 1. Core Architecture (Clean Architecture + MVVM)

The application strictly adheres to the **Clean Architecture** pattern to separate concerns and ensure that the UI, Business Logic, and Data layers are completely decoupled.

### Layers & Data Flow
1. **Presentation Layer (UI & ViewModels)**
   - Built entirely in **Jetpack Compose**.
   - **ViewModels** (e.g., `RemindersViewModel`) manage state using `StateFlow` and `MutableStateFlow`.
   - The UI observes the state (`collectAsState()`) and triggers events (e.g., `processInput()`) back to the ViewModel.
   - **Why?** Unidirectional Data Flow (UDF) guarantees predictable UI states and prevents race conditions during recomposition.

2. **Domain/Service Layer (Business Logic)**
   - Contains the `AIService` which acts as the "Brain".
   - It takes raw strings from the ViewModels and communicates with the network to return strongly typed `Result<TaskParsingResult>` objects.

3. **Data Layer (Local Storage)**
   - Contains `Room` Entities (`TaskEntity`, `ExpenseEntity`, `HabitEntity`) and DAOs.
   - The data is exposed via Kotlin `Flow<List<T>>`, meaning the UI updates automatically whenever the database changes without requiring manual refresh calls.

### Dependency Injection (Hilt)
- **What we used:** `dagger.hilt.android`
- **Implementation:** `LifeOSApplication` is annotated with `@HiltAndroidApp`. We use `@Module` and `@InstallIn(SingletonComponent::class)` in `DatabaseModule.kt` to provide singleton instances of our DAOs and the `AIService`.
- **Why?** Hilt removes massive amounts of boilerplate factory code. By injecting `TaskDao` directly into the `RemindersViewModel` constructor, testing becomes trivial (we can easily pass a Mock DAO during Unit Tests).

---

## 🎨 2. UI/UX Implementation Details (Premium Update)

### The Premium Design System (`ui/theme`)
The application features a bespoke, high-end design system inspired by top-tier productivity tools (Notion, Linear, Superhuman).
- **Dynamic Theme Modes:** Supports `Light`, `Dark`, and `AmoledBlack` (true black for OLED screens to save battery).
- **Accent Color Engine:** Users can dynamically switch between 5 distinct accent palettes (Purple, Emerald, Cyan, Orange, Royal Blue). This doesn't just change a single hex code; it mathematically alters the `primary`, `secondary`, and `tertiary` container bounds.
- **Local Composition:** Used `CompositionLocalProvider` (`LocalPremiumColors`) to pass down complex, non-standard design tokens (like `StreakFlame`, `GlassOverlay`, and `CardGlow`) deep into the Compose tree without cluttering function signatures.

### Custom Component Library (`ui/components` & `ui/animation`)
Instead of relying solely on generic Material 3 elements, we built a proprietary component library:
- **`PremiumBottomNav.kt` & `PremiumTopBar.kt`:** Replaces standard bars with glassmorphic, elevated equivalents.
- **`FloatingAIButton.kt`:** A multi-action floating action button overlay that handles fast deep-linking.
- **`ShimmerLoading.kt`:** Instead of circular progress indicators, the app uses skeleton shimmering rectangles for data loading, creating a perceived performance boost.
- **`EmptyState.kt`:** Engaging empty states with emojis and clear call-to-actions when lists are empty.
- **Micro-Animations:** Heavy usage of `animateFloatAsState(spring())` ensures every button press scales down slightly (`0.95f`), creating a deeply tactile, responsive feel. Screen transitions utilize custom enter/exit sliding fades.

---

## 🧠 3. Artificial Intelligence & Machine Learning Pipeline

### A. NLP Parsing (OpenAI via Retrofit)
- **The Problem:** Converting messy text ("remind me to pay electric bill 50 bucks tmrw") into structured data.
- **The Flow:**
  1. User types in `InputBar.kt`.
  2. `RemindersViewModel` calls `AIService.parseInput()`.
  3. `AIService` utilizes **Retrofit2** to send an HTTP POST to `https://api.openai.com/v1/chat/completions`.
  4. We instruct the LLM via a strict *System Prompt* to return ONLY valid JSON.
  5. The JSON response is automatically deserialized by **Gson** into Kotlin data classes (`TaskParsingResult` or `ExpenseParsingResult`).
  6. The ViewModel maps this result into a `TaskEntity` and calls `taskDao.insertTask()`.

### B. OCR Bill Scanning (CameraX + ML Kit)
- **The Problem:** Extracting text from physical receipts.
- **Implementation:**
  - `CameraScanScreen.kt` uses **CameraX** `ProcessCameraProvider` to bind the lifecycle and preview the camera.
  - When the user captures an image, the `ImageProxy` is converted into a `InputImage` and fed to `TextRecognition.getClient()`.
  - **Why ML Kit?** It processes the image entirely offline on the edge device, ensuring extreme privacy for financial documents. The extracted raw string block is then routed to the `AIService` (OpenAI) to intelligently pluck out the merchant name and total amount.

### C. Voice Input (SpeechRecognizer & Permissions)
- **Implementation:** `VoiceHelper.kt` wraps the Android native `SpeechRecognizer`. When the mic is tapped, `rememberLauncherForActivityResult` requests `RECORD_AUDIO` permissions dynamically. If granted, it streams audio to the OS engine and returns the transcribed string in `onResults()`.

### D. Memory Engine (Context-Aware Querying)
- **Implementation:** `MemoryScreen.kt` provides an interface to ask questions about past logs (e.g., "Where did I put my keys?").
- **Flow:** `MemoryViewModel.kt` takes the question, queries the local Room database for relevant context, and passes both the question + local context to the `AIService`. The AI then generates an answer based *only* on the provided local data.

### E. TFLite Prediction Engine (Local ML)
- **Implementation:** Built a framework in `MLManager.kt` designed to load `.tflite` models via Android's ML binding.
- **Purpose:** Used for offline, privacy-first predictions (like predicting when a user usually completes a habit). Currently implemented as a highly structured stub ready for a trained model asset.

### F. Vacation Mode & Streak Logic
- **Implementation:** `HabitsViewModel.kt` contains logic to increment `currentStreak` based on `lastCompletedDate`. 
- **Feature:** A "Vacation Mode" boolean prevents streaks from resetting to 0 if the user takes time off, solving a major UX pain point in traditional habit trackers.

---

## 🔒 4. Local Database & Security Model

Since this is a "Personal OS", data privacy is the absolute highest priority.

### Room Database Schema
- `TaskEntity`: `id`, `title`, `description`, `dueDate` (ISO-8601 string), `priority` (high/medium/low), `isCompleted`.
- `ExpenseEntity`: `id`, `merchant`, `amount`, `category`, `timestamp`.
- `HabitEntity`: `id`, `name`, `currentStreak`, `lastCompletedDate`.

### SQLCipher (Encryption at Rest)
- **Implementation:** In `DatabaseModule.kt`, instead of initializing a standard Room DB, we inject a `SupportFactory` from `net.zetetic.sqlcipher`.
- **Key Management:** We created `KeyStoreHelper.kt`. It uses the `AndroidKeyStore` provider to securely generate a 256-bit AES key. This key never leaves the hardware-backed secure enclave. The key is used as the passphrase for SQLCipher.
- **Why?** If a malicious app gains root access, or someone physically extracts the `.db` file via USB, the data is completely scrambled and useless without the hardware keystore.

### Biometric Gate
- **Implementation:** In `MainActivity.kt`, the `setContent` block is wrapped in an `if (isAuthenticated)` check. `BiometricPrompt` forces the user to provide a Face ID or Fingerprint before the Compose UI is even allowed to render.

---

## 📱 5. Android OS Integrations (Widgets & System UI)

### A. Home Screen Widgets (Jetpack Glance)
- **Implementation:** `LifeOSWidget.kt` uses `androidx.glance`. We registered a `GlanceAppWidgetReceiver` in `AndroidManifest.xml`.
- **Functionality:** It provides a lightweight Compose-based UI directly on the Android home screen, displaying pending tasks and a button that deep-links directly into the Expenses screen.

### B. Native Splash Screen & Adaptive Icons
- **Implementation:** Used the `androidx.core:core-splashscreen` API. `MainActivity.kt` calls `installSplashScreen()` before `onCreate`.
- **Branding:** Created a vector-based Adaptive App Icon (`ic_launcher.xml` + `ic_launcher_foreground.xml`) to ensure the app icon dynamically shapes to any Android launcher (Squircle, Teardrop, Circle).

---

---

## ☁️ 6. Cloud Backup (Supabase & WorkManager)

- **The Problem:** The user wants an offline-first app, but needs a way to recover data if they lose their phone.
- **Implementation:** 
  - We use the **Supabase Kotlin SDK** (which internally uses `Ktor` for networking).
  - In `SettingsScreen.kt`, the user flips a toggle. This enqueues a `PeriodicWorkRequestBuilder<SyncWorker>` via **WorkManager**.
  - `SyncWorker.kt` runs in the background. It reads the local Room database snapshot (using `.first()` on the Flows) and pushes JSON blobs to Supabase Postgres tables using `.upsert()`.
  - **Why WorkManager?** It respects Doze mode and guarantees execution even after phone reboots.

---

## 🛡 7. DevOps, CI/CD, & Obfuscation

- **ProGuard / R8:** Enabled `isMinifyEnabled = true` in `build.gradle.kts`. This shrinks the APK size and obfuscates the code (renames variables to `a.b.c`). Custom rules in `proguard-rules.pro` ensure that reflection-heavy libraries like Room and Retrofit don't crash in production.
- **Testing:** `JUnit4`, `MockK` (for faking the Database and Network responses), and `Turbine` (for asserting sequences of events in our Compose StateFlows).
- **GitHub Actions:** `.github/workflows/android.yml` automatically sets up JDK 17, mocks the keystore properties, and runs `./gradlew testDebugUnitTest` and `./gradlew assembleDebug` on every push to the repository, ensuring code stability.

---

## 📂 8. Complete Project Structure

Here is the exact layout of the codebase and the specific purpose of every important file:

```text
Managing app/
├── .github/workflows/
│   └── android.yml                -> CI/CD pipeline for automated testing and APK builds.
├── app/build.gradle.kts           -> App-level dependencies (Room, Hilt, Compose, Supabase, ML Kit) and Release configurations (ProGuard, Signing).
├── generate_keystore.ps1          -> PowerShell script to generate `release-keystore.jks` for Play Store distribution.
├── proguard-rules.pro             -> Custom R8 obfuscation rules to protect the app without breaking Retrofit/Room.
└── app/src/main/
    ├── AndroidManifest.xml        -> App configuration. Requests permissions (Mic, Biometrics, Internet) and registers the Home Screen Widget.
    ├── res/
    │   ├── drawable/ic_launcher_foreground.xml -> The custom vector icon for the app.
    │   ├── mipmap-anydpi-v26/ic_launcher.xml   -> The Adaptive Icon wrapper for Android 8+.
    │   ├── values/themes.xml                   -> Defines the `Theme.App.Starting` for the native Splash Screen.
    │   └── xml/lifeos_widget_info.xml          -> Defines the dimensions and rules for the Jetpack Glance Widget.
    └── java/com/personal/lifeos/
        ├── LifeOSApplication.kt   -> The Hilt entry point `@HiltAndroidApp`. Mandatory for Dependency Injection.
        ├── MainActivity.kt        -> The single Activity. Handles `installSplashScreen()`, `BiometricPrompt` security, and `NavHost` routing.
        │
        ├── ai/                    -> (Artificial Intelligence Module)
        │   ├── AIService.kt       -> The core brain. Converts string requests to Network calls, parses the JSON, and handles errors.
        │   ├── OpenAIApi.kt       -> Retrofit interface detailing the `POST /chat/completions` endpoint.
        │   ├── OpenAIModels.kt    -> Data classes mapping exactly to OpenAI's request/response JSON format.
        │   └── di/NetworkModule.kt-> Hilt module providing a Singleton `Retrofit` instance with Gson converter.
        │
        ├── core/                  -> (Core/Shared Module)
        │   ├── data/local/        -> Local Storage
        │   │   ├── LifeOSDatabase.kt -> The Room Database configuration.
        │   │   ├── KeyStoreHelper.kt -> Generates and retrieves the 256-bit AES encryption key from the Android Hardware Keystore.
        │   │   ├── dao/           -> Data Access Objects (SQL queries) for `ExpenseDao`, `HabitDao`, `TaskDao`.
        │   │   └── entity/        -> The SQL table definitions (`ExpenseEntity`, `HabitEntity`, `TaskEntity`).
        │   ├── di/DatabaseModule.kt  -> Hilt module that provides the DAOs and attaches `SQLCipher` to Room using the Keystore AES key.
        │   ├── di/WorkManagerModule.kt -> Hilt module to inject DAOs into background workers.
        │   └── ui/VoiceHelper.kt  -> A wrapper for Android's `SpeechRecognizer` to easily convert speech-to-text.
        │
        ├── expenses/              -> (Financial Tracking Module)
        │   └── ui/
        │       ├── CameraScanScreen.kt -> Uses CameraX and Google ML Kit to take a picture of a receipt and extract text.
        │       ├── ExpensesScreen.kt   -> UI for tracking money. Uses `Vico` charts to draw graphs of spending habits.
        │       └── ExpensesViewModel.kt-> Manages financial state and triggers the AI to parse the ML Kit receipt text.
        │
        ├── habits/                -> (Habit Tracking Module)
        │   └── ui/
        │       ├── HabitsScreen.kt     -> UI for checking off daily habits.
        │       └── HabitsViewModel.kt  -> Business logic for tracking streaks and enabling "Vacation Mode" to freeze streaks.
        │
        ├── memory/                -> (Semantic Memory Engine Module)
        │   └── ui/
        │       ├── MemoryScreen.kt     -> UI to ask the AI questions about past logs (e.g. "Where did I leave my keys?").
        │       └── MemoryViewModel.kt  -> Pulls the local Room DB context and feeds it to `AIService` so the AI can answer personal questions.
        │
        ├── ml/                    -> (Local Machine Learning Module)
        │   └── MLManager.kt       -> A framework class built to load and execute `.tflite` models for offline predictions (e.g., predicting habit completion times).
        │
        ├── reminders/             -> (Tasks & Reminders Module)
        │   └── ui/
        │       ├── RemindersScreen.kt  -> The main To-Do list UI. Contains the shared `InputBar` (Mic + Text field).
        │       └── RemindersViewModel.kt-> Handles checking off tasks and sending voice/text input to the AI to create new tasks.
        │
        ├── sync/                  -> (Cloud Backup Module)
        │   ├── SettingsScreen.kt  -> UI to toggle Cloud Backup and dynamically switch Theme/Accent colors.
        │   └── SyncWorker.kt      -> A `CoroutineWorker` that runs in the background, serializes the Room DB, and pushes it to Supabase via Ktor `postgrest`.
        │
        ├── ui/                    -> (Premium UI Framework)
        │   ├── animation/         -> Custom screen transitions and spring animation constants.
        │   ├── components/        -> Custom component library (`PremiumBottomNav`, `PremiumTopBar`, `FloatingAIButton`, `ShimmerLoading`, `EmptyState`).
        │   └── theme/             -> (Global Theming Engine)
        │       ├── Color.kt       -> Defines the massive premium color palette across Light, Dark, and AMOLED tiers.
        │       ├── Theme.kt       -> Custom `LifeOSTheme` factory binding `ThemeMode` and `AccentColor` together.
        │       ├── ThemeState.kt  -> Enum classes for user preferences.
        │       └── Type.kt        -> Custom typography scaling.
        │
        └── widgets/               -> (Home Screen Widgets)
            ├── LifeOSWidget.kt    -> The Compose-based Jetpack Glance UI for the Android home screen widget.
            └── LifeOSWidgetReceiver.kt -> The BroadcastReceiver required by Android to update the widget state.
```
