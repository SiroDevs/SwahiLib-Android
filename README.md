# SwahiLib - Kamusi ya Kiswahili

**SwahiLib** is a beautifully crafted Android app that lets users explore and search through a rich collection of **Swahili words, idioms, sayings, and proverbs**, with support for offline access, clean UI, and real-time updates.

This version is built using **Jetpack Compose**, **Room**, **Hilt**, and backed by **Supabase** for remote data.

> 🔗 iOS Version Repo: [@SiroDaves/SongLib-iOS](https://github.com/SiroDaves/SongLib-iOS)

<a href='https://play.google.com/store/apps/details?id=com.swahilib'>
  <img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width='200'/>
</a>

## ✨ Screenshots
<table>
    <tr>
        <td><img src="screenshots/image1.png" width="200px" /></td>
        <td><img src="screenshots/image2.png" width="200px" /></td>
        <td><img src="screenshots/image3.png" width="200px" /></td>
    </tr>
</table>

## ✨ Features

* 🔍 **Search** for Swahili **words**, **idioms**, **sayings**, and **proverbs**
* 📘 **View details** by tapping on any result

    * See **synonyms** for words and proverbs where available
* 💾 **Offline-first** support using **Room Database**
* 💉 **Dependency injection** powered by **Hilt**
* 💫 **Smooth animations** with **Lottie**
* ⚡ Uses **Supabase** + **Ktor** for fast backend communication

## 🧰 Tech Stack

### UI & Architecture

* Jetpack Compose (Material 3, Navigation, LiveData, Previews)
* Hilt for Dependency Injection
* Room for local database
* Kotlinx Serialization
* Jan Tennert’s [Supabase Kotlin Client](https://github.com/jaumard/supabase-kt)
* Retrofit & Ktor for HTTP networking
* Lottie Compose for animations

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/SiroDaves/SongLib-Android.git
cd SongLib-Android
```

### 2. Set up Supabase Configuration

Create a `.env` file at the root of the project by copying the template:

```bash
cp .env.example .env
```

Update the `.env` file with your actual Supabase credentials (URL, API key, etc.)

### 3. Open in Android Studio

Open the project in the latest version of **Android Studio** (Giraffe or later recommended for best Jetpack Compose support).


### 4. Build the Project

The app uses Gradle version catalogs for dependencies. Android Studio should sync and resolve everything automatically. If not, run:

```bash
./gradlew clean build
```

Or use **Sync Project with Gradle Files** in the IDE.

### 5. Run the App

Connect a physical Android device or use an emulator, then click **Run** or press:

```
Shift + F10
```

## 📄 Notes

* The app syncs content from Supabase and stores it in Room for offline usage.
* Data updates are triggered automatically via ViewModel logic.
* All dependencies are managed using **libs.versions.toml** for cleaner and centralized version control.

## 📦 Main Libraries Used

| Category             | Library                                 |
| -------------------- | --------------------------------------- |
| UI                   | Jetpack Compose (Material2 & Material3) |
| DI                   | Hilt                                    |
| Database             | Room                                    |
| Animations           | Lottie Compose                          |
| Supabase Integration | Supabase Kotlin (PostgREST)             |
| Networking           | Ktor, Retrofit, OkHttp                  |
| Serialization        | kotlinx.serialization.json              |

## 🛠 License

This project is open-source and available under the MIT License.
