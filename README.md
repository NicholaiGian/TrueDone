TrueDone – AI-Powered Task Tracker
A native Android app where you prove task completion with before & after photos, then let AI verify the result.

## 📌 Overview
TrueDone goes beyond a simple to-do list. For each task, you capture a Before photo before you start and an After photo when you're done.
The app sends both images to the Google Gemini API, which checks whether the task was actually completed and gives you a short summary with suggestions.

## ✨ Core Features
* **AI Task Verification:** Multimodal AI analysis compares before/after photos to validate work and suggest improvements.
* **Native Camera & Media Integration:** Securely capture images using the device's native camera hardware or select existing photos from the gallery.
* **Swipeable Comparison UI:** Intuitive `ViewPager2` implementation allowing users to seamlessly swipe between before and after states.
* **Secure User Authentication:** Full login and registration flow with session management.
* **Offline-First Storage:** Fast and reliable local data persistence for users and tasks using the Room SQLite database.
* **Dynamic Theming:** Modern Material Design 3 UI with user-configurable Light and Dark mode preferences.

## 🛠️ Technology Stack
* **Language:** Java
* **Platform:** Native Android SDK (Min SDK 24 / Target SDK 36)
* **Architecture:** Model-View-Presenter (MVP) for clean separation of concerns.
* **Database:** Room Persistence Library (SQLite) via background asynchronous threads.
* **Networking:** Retrofit2 & Gson for safe HTTP API calls.
* **AI Integration:** Google Gemini 2.5 Flash REST API.
* **UI & Media:** Material Design 3, ViewBinding, and Glide.

## 🧪 Testing & Quality Assurance
To ensure a robust user experience, this application was built with a focus on quality and edge-case management:
* **API Error Handling:** Graceful degradation and user-friendly error messages for network timeouts, invalid JSON parsing, or missing API keys.
* **Input Validation:** Strict UI constraints and error states for authentication fields.
* **Memory Management:** Optimized Bitmap scaling calculations to preserve image aspect ratios while preventing `OutOfMemory` exceptions before network transmission.

## 🚀 Installation & Setup
To run this project locally, you will need Android Studio and a valid Google Gemini API Key.

1. **Clone the repository or download the ZIP File:**
   ```bash
   git clone https://github.com/NicholaiGian/TrueDone.git

2. **Open the project:** in Android Studio and let Gradle sync finish.
3. **Configure the API Key:"**
   * *No API key?* You can generate a free one at [Google AI Studio](https://aistudio.google.com/app/apikey).
   * Navigate to the root folder of the project.
   * Open the `local.properties` file (create one if it does not exist).
   * Add your Gemini API key:
     ```
     GEMINI_API_KEY=your_api_key_here
     ```
   * Build and Run the application on an emulator or physical device.

4. **Generate a Release APK:**

   You might be needed to create your own signing keystore to generate a signed release APK.

   * In Android Studio, go to **Build → Generate Signed App Bundle / APK**.
   * Select **APK**, then click **Next**.
   * Under *Key store path*, click **Create new...** to generate a new keystore file.
   * Fill in the required fields:
      * **Key store path** — choose a safe location outside the project directory.
      * **Password** — set a strong keystore password.
      * **Alias** — give your key an alias (e.g., `truedone-key`).
      * **Key password** — set a password for the key itself.
      * **Certificate fields** — fill in at least your name or organization.
   * Click **OK**, then **Next**.
   * Select the **release** build variant.
   * Click **Finish**. Android Studio will build and place the signed APK in:
```
     app/release/app-release.apk
```
* Rename the apk if needed with app version.
* Note: Keep your keystore file and passwords in a safe place. If you lose them, you will not be able to sign future updates with the same key. It is recommended to store the keystore outside the project directory and never commit it to version control.

## 🔭 Suggested Future Improvements
* **Cloud Sync** — Migrate from local-only Room storage to a backend (e.g., Firebase Firestore) for cross-device task access.
* **Task Categories & Tags** — Organize tasks by project, priority, or custom label.
* **Notifications & Reminders** — WorkManager-powered reminders for incomplete tasks.
* **AI History & Trends** — Aggregate Gemini analysis results over time to surface productivity insights and improvement patterns.
* **Export & Sharing** — Export task reports (PDF or image) including Before/After photos and AI summaries.
* **Widget Support** — Android home screen widget for quick task status at a glance.
   
