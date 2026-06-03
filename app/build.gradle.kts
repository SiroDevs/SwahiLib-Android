import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.devtools.ksp)
    kotlin("plugin.serialization") version "2.1.21"
    id("kotlin-parcelize")
    alias(libs.plugins.io.sentry)
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore/key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

android {
    compileSdk = 37

    defaultConfig {
        applicationId = "com.swahilib"
        versionCode = 155
        versionName = "1.0.155"
        minSdk = 26
        targetSdk = 37

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SupabaseUrl", "\"${localProperties.getProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SupabaseKey", "\"${localProperties.getProperty("SUPABASE_ANON_KEY")}\"")
        buildConfigField("String", "PesapalConsumerKey", "\"${localProperties.getProperty("PESAPAL_CONSUMER_KEY") ?: ""}\"")
        buildConfigField("String", "PesapalConsumerSecret", "\"${localProperties.getProperty("PESAPAL_CONSUMER_SECRET") ?: ""}\"")
        buildConfigField("String", "PesapalIpnId", "\"${localProperties.getProperty("PESAPAL_IPN_ID") ?: ""}\"")
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
            storeFile = keystoreProperties["storeFile"]?.let { file(it as String) }
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
    implementation(project(":core:designsystem"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))

    // Feature modules
    implementation(project(":feature:splash"))
    implementation(project(":feature:init"))
    implementation(project(":feature:home"))
    implementation(project(":feature:advsearch"))
    implementation(project(":feature:word"))
    implementation(project(":feature:idiom"))
    implementation(project(":feature:proverb"))
    implementation(project(":feature:saying"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:howitworks"))
    implementation(project(":feature:help"))
    implementation(project(":feature:donation"))

    // Navigation
    implementation(libs.compose.navigation)
    implementation(libs.compose.hilt.navigation)

    // Activity
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Monitoring
    implementation(libs.android.billing)
    implementation(libs.androidx.concurrent.futures)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}
