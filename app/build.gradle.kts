import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
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

val configProperties = Properties()
val configFile = rootProject.file("gradle/config/config.properties")
if (configFile.exists()) {
    configProperties.load(configFile.inputStream())
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.swahilib"
    compileSdk = (configProperties["targetSdk"] as String).toInt()

    composeOptions {
        kotlinCompilerExtensionVersion = "1.8.1"
    }

    defaultConfig {
        applicationId = "com.swahilib"
        minSdk = (configProperties["minSdk"] as String).toInt()
        targetSdk = (configProperties["targetSdk"] as String).toInt()
        versionCode = (configProperties["versionCode"] as String).toInt()
        versionName = configProperties["versionName"] as String
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SupabaseUrl", "\"${localProperties.getProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SupabaseKey", "\"${localProperties.getProperty("SUPABASE_ANON_KEY")}\"")
        buildConfigField("String", "RcCatId", "\"${localProperties.getProperty("REVENUECAT_ID")}\"")
        buildConfigField("String", "RcApiKey", "\"${localProperties.getProperty("REVENUECAT_API_KEY")}\"")
        buildConfigField("String", "SentryDsn", "\"${localProperties.getProperty("SENTRY_DSN")}\"")
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
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("staging") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            applicationIdSuffix = ".stg"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        disable += "NullSafeMutableLiveData"
    }
}

sentry {
    debug.set(true)
    includeSourceContext.set(true)
    org.set("futuristicken")
    projectName.set("swahilib-android")
    additionalSourceDirsForSourceContext.set(setOf("detail/src/main/java", "core/src/main/java"))
    authToken.set(localProperties.getProperty("SENTRY_AUTH_TOKEN"))
}

configurations.all {
    exclude(group = "com.google.guava", module = "listenablefuture")
}

dependencies {
//    implementation(project(":core"))
//    implementation(project(":data"))
//    implementation(project(":domain"))
//    implementation(project(":presentation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.icons.extended)
    implementation(libs.compose.navigation)
    implementation(libs.compose.hilt.navigation)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compiler)
    implementation(libs.androidx.foundation)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.lottie.compose)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.android.compiler)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3.logging)
    implementation(platform(libs.jan.tennert.supabase.bom))
    implementation(libs.jan.tennert.supabase.postgrest)
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.android.billing)
    implementation(libs.revenuecat)
    implementation(libs.revenuecat.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}