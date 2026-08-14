import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    alias(libs.plugins.io.sentry)
    alias(libs.plugins.google.services)
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore/key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    compileSdk = 37

    defaultConfig {
        applicationId = "com.swahilib"
        versionCode = 184
        versionName = "1.0.184"
        minSdk = 26
        targetSdk = 37

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["KEY_ALIAS"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD"] as String
            storePassword = keystoreProperties["KEYSTORE_PASSWORD"] as String
            storeFile = keystoreProperties["STORE_FILE"]?.let { file(it as String) }
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        disable += "NullSafeMutableLiveData"
    }
    namespace = "com.swahilib"
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

sentry {
    debug.set(true)
    includeSourceContext.set(true)
    org.set("futuristicken")
    projectName.set("swahilib-android")
    authToken.set(localProperties.getProperty("SENTRY_AUTH_TOKEN"))
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:design_system"))
    implementation(project(":core:engagement"))
    implementation(project(":core:games"))
    implementation(project(":core:network"))
    implementation(project(":core:social"))
    implementation(project(":core:ui"))

    // Feature modules
    implementation(project(":feature:home"))
    implementation(project(":feature:likes"))
    implementation(project(":feature:history"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:social"))
    implementation(project(":feature:advanced_search"))
    implementation(project(":feature:word"))
    implementation(project(":feature:idiom"))
    implementation(project(":feature:proverb"))
    implementation(project(":feature:saying"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:how_it_works"))
    implementation(project(":feature:help"))
    implementation(project(":feature:donation"))
    implementation(project(":feature:daily_content"))
    implementation(project(":feature:progress"))
    implementation(project(":feature:quiz"))
    implementation(project(":feature:word_builder"))
    implementation(project(":feature:sentence_builder"))
    implementation(project(":feature:spelling"))
    implementation(project(":feature:crossword"))
    implementation(project(":feature:sudoku"))
    implementation(project(":feature:hangman"))

    // Android Room
    implementation(libs.androidx.room.runtime)

    // Navigation
    implementation(libs.compose.navigation)
    implementation(libs.compose.hilt.navigation)

    // Activity
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Monitoring
    implementation(libs.android.billing)
    implementation(libs.androidx.concurrent.futures)

    // WorkManager — initialized in SwahiLibApp with Hilt-provided Configuration
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Google Sign-In (Credential Manager)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.identity.googleid)

    // Firebase Auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}
