# Personal AI Life OS 🧠📱

![Android](https://img.shields.io/badge/Platform-Android_14-3DDC84?style=flat-square&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=flat-square&logo=android&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean_Architecture-FF9800?style=flat-square)
![OpenAI](https://img.shields.io/badge/AI-OpenAI-412991?style=flat-square&logo=openai&logoColor=white)

Welcome to the **Personal AI Life OS**, a highly intelligent, offline-first Android application designed to act as your digital second brain. Instead of forcing you into rigid forms, you simply type, speak, or snap a photo of what you want to do, and the AI organizes it for you. 

---

## 🌟 Product Showcase (Features)

This application is built entirely around **Zero-Friction Input**.

### 1. Reminders & Tasks 📝
Stop filling out forms. Simply tell the app: *"Remind me to buy milk tomorrow at 5 PM."* 
The onboard AI Service extracts the title, sets the exact `dueDate`, and categorizes the priority automatically.

### 2. Expense Tracking (with OCR) 💸
Just bought lunch? Either:
- **Type/Speak:** *"Spent $15 on a sandwich at Subway."*
- **Snap a Pic:** Use the built-in Camera (powered by **Google ML Kit**) to scan your receipt. 

The AI extracts the merchant and amount, logging it directly into your local database. View your spending trends instantly on a beautiful, dynamic chart.

### 3. Habits & Vacation Mode 🧘
Track your daily habits and maintain your streaks. If you go on vacation, toggle **Vacation Mode** to freeze your streaks so you aren't penalized for taking time off!

### 4. Memory Engine 🧠
Have a random thought? *"I left my spare keys in the kitchen drawer."* 
Log it into the Memory tab. Later, ask the AI: *"Where did I leave my keys?"* and it will search your local database to give you the answer.

### 5. Maximum Security 🔒
- **Biometric Lock:** The app requires your fingerprint/face to open.
- **SQLCipher:** Your Room SQLite database is encrypted at rest using an AES key generated securely in the Android Hardware Keystore.

### 6. Cloud Backup ☁️
Optional **Supabase** sync. Toggle it on in Settings, and an Android `WorkManager` background job will silently serialize your data and push it to your private cloud storage.

---

## 🛠 Developer Guide (Architecture)

This project strictly follows **Clean Architecture** principles and is built using modern Android standards (MAD).

### Tech Stack
* **UI**: Jetpack Compose (Material 3), Jetpack Glance (for Home Screen Widgets)
* **DI**: Hilt (Dependency Injection)
* **Local DB**: Room (with SQLCipher encryption)
* **Network**: Ktor (for Supabase), Retrofit (for OpenAI)
* **Background**: WorkManager (for cloud sync)
* **Machine Learning**: Google ML Kit (Text Recognition OCR), TensorFlow Lite (Time Prediction stubs)

### Module Structure
The codebase is structured by feature, ensuring maximum scalability:
* `:core` - Base models, DI modules, `DatabaseModule.kt`, `VoiceHelper.kt`.
* `:reminders` - Task management and AI processing logic.
* `:expenses` - Financial tracking, Vico Charts, and CameraX ML Kit scanning.
* `:habits` - Streak logic and Vacation Mode toggles.
* `:memory` - Natural language querying of stored data.
* `:ai` - Retrofit client interfacing with OpenAI's Chat Completions API.
* `:sync` - WorkManager pushing JSON snapshots to Supabase.

### Data Flow
1. **View**: User interacts with a Compose Screen (e.g., `RemindersScreen.kt`).
2. **ViewModel**: The `RemindersViewModel` updates a `StateFlow` and calls the `AIService`.
3. **Domain/Data**: `AIService` parses natural language via Retrofit into a `TaskEntity`, which is then inserted into the encrypted Room `TaskDao`.

---

## 🚀 Setup & Installation

### 1. Requirements
- Android Studio Iguana (or newer)
- JDK 17
- An active OpenAI API Key

### 2. Clone & Keys
Clone this repository and create a file named `local.properties` in the root directory. Add your keys:

```properties
OPENAI_API_KEY=sk-your-key-here
# Optional for Cloud Sync:
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```

### 3. Generate Release Keystore (For Production)
If you want to build a highly optimized, obfuscated release APK, run the included PowerShell script from the root directory:
```powershell
.\generate_keystore.ps1
```
This will generate `release-keystore.jks` and print the exact lines you need to paste into your `local.properties` file for Gradle to sign the app.

### 4. Build & Run
- **Debug:** Hit the Play button in Android Studio.
- **Release:** Go to `Build -> Generate Signed Bundle/APK`, select your release config, and install the optimized APK on your device!

### 5. Running Tests
The project includes a robust suite of Unit Tests utilizing **JUnit4**, **MockK**, and **Turbine**.
Run the suite via terminal:
```bash
./gradlew testDebugUnitTest
```

---
*Built with ❤️ and AI.*
