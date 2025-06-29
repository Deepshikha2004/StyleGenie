


# 👗 StyleGenie: AI-Powered Fashion Chatbot

StyleGenie is a modern Android app that helps users discover fashion items using AI-powered multimodal search. Users can ask questions using text or voice, or upload images to receive intelligent outfit recommendations. It features a sleek chat interface, Firebase authentication, and backend integration via Retrofit.

---

## 🚀 Features

- 💬 **Fashion Chatbot** – Ask about outfit styles like “pastel summer dress” and get smart replies.
- 🖼️ **Image Search** – Upload a fashion image to discover similar styles.
- 🗣️ **Voice Input** – Speak fashion-related queries using Android’s speech recognizer.
- 🔐 **Firebase Authentication** – Secure login and signup with Firebase.
- 🔄 **Chat Reset** – Clear chat anytime to restart the conversation.
- 🧠 **Backend Integration** – Connects to a Python AI server over LAN for recommendation responses.
- 🔍 **Google Sign-In** – Easy login using your Google account.

---

## 🛠️ Tech Stack

| Layer         | Technology                        |
|---------------|------------------------------------|
| Language      | Kotlin                             |
| UI            | XML                                |
| Networking    | Retrofit           |
| Image Loading | Glide                              |
| Backend       | Python (Flask/FastApi over LAN)     |
| Auth & DB     | Firebase Auth & Realtime Database  |
| Voice Input   | Android SpeechRecognizer           |

---


## 🗂 Project Structure (Key Components)

```

StyleGenie/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/stylegenie/
│   │   │   │   ├── ChatBotActivity.kt         # Core chatbot UI & logic
│   │   │   │   ├── HomeActivity.kt            # Main product screen
│   │   │   │   ├── LoginActivity.kt           # Firebase login
│   │   │   │   ├── SignupActivity.kt          # Firebase signup
│   │   │   │   ├── OnboardingActivity.kt      # First launch intro
│   │   │   │   ├── network/
│   │   │   │   │   ├── ApiService.kt          # API endpoints
│   │   │   │   │   └── RetrofitInstance.kt    # Retrofit client & BASE\_URL
│   │   └── res/layout/                        # All XML UI layouts
│   ├── AndroidManifest.xml                    # Permissions & activity declarations
├── build.gradle (app)                         # Dependencies & plugins

````

---

## 📦 Dependencies

#### Gradle Plugins

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt") // For Glide or Room
}
````

#### Libraries

```kotlin
// AndroidX
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
implementation("androidx.cardview:cardview:1.0.0")
implementation("com.google.android.material:material:1.11.0")

// Firebase
implementation("com.google.firebase:firebase-auth:22.3.1")
implementation("com.google.firebase:firebase-analytics:21.6.1")
implementation("com.google.firebase:firebase-database-ktx:20.3.0")
implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

// Google Sign-In
implementation("com.google.android.gms:play-services-auth:21.0.0")

// Retrofit & Gson
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Glide
implementation("com.github.bumptech.glide:glide:4.16.0")
kapt("com.github.bumptech.glide:compiler:4.16.0")

// Circle Image View
implementation("de.hdodenhof:circleimageview:3.1.0")

// JSON Parsing
implementation("com.google.code.gson:gson:2.10.1")

// Testing
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
```

---

## 🔐 Permissions

Declared in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" tools:targetApi="33" />
<uses-permission android:name="android.permission.CAMERA" />
```

---

## 🌐 Retrofit Configuration

### ✅ `RetrofitInstance.kt`

```kotlin
private const val BASE_URL = "http://192.168.206.239:8000/"
```

> Ensure your Python backend runs with:

```bash
python manage.py runserver 0.0.0.0:8000
```

### ✅ `ApiService.kt`

```kotlin
@POST("search")
fun getFashionByText(@Body body: Map<String, String>): Call<BotResponse>

@Multipart
@POST("upload-and-search")
fun getFashionByImage(@Part file: MultipartBody.Part): Call<BotResponse>
```

---

## 🧪 How to Run the Project

1. Clone the repo:

   ```bash
   git clone https://github.com/Deepshikha2004/StyleGenie.git
   ```

2. Open in **Android Studio**

3. Update your IP address in:

   ```kotlin
   RetrofitInstance.kt → BASE_URL
   ```

4. Run backend server on your local machine using:

   ```bash
   python manage.py runserver 0.0.0.0:8000
   ```

5. Connect your **Android device to the same Wi-Fi**.

6. Run the app and test voice, image, and text queries.

---

## 🔮 Future Enhancements

* 💡 Outfit mix-and-match suggestions
* 🔥 Real-time trend recommendations
* 📥 Download or save looks to favorites
* 🎨 Fashion color/style filters
* 🔗 Cloud backend with Firebase Functions

---

## 👩‍💻 Developed By

**Deepshikha Singh**
B.Tech CSE, Jaypee University of Engineering and Technology
📧 [deepshikha.stylegenie@gmail.com](mailto:deepshikha.stylegenie@gmail.com)
🔗 [LinkedIn](https://www.linkedin.com/in/deepshikha-singh-6378a624a)
🔗 [GitHub](https://github.com/Deepshikha2004)

---
