import java.util.Properties

plugins {
    alias(libs.plugins.swahilib.android.app)
    alias(libs.plugins.swahilib.android.app.compose)
    alias(libs.plugins.swahilib.android.app.jacoco)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.swahilib.supabase)
    alias(libs.plugins.swahilib.android.room)
    id("kotlin-parcelize")
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore/key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    defaultConfig {
        applicationId = "com.swahilib"
        versionCode = 150
        versionName = "1.0.150"
        testInstrumentationRunner = "com.swahilib.core.testing.AppTestRunner"

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
        release {
            isMinifyEnabled = providers.gradleProperty("minifyWithR8")
                .map(String::toBooleanStrict).getOrElse(true)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
//            baselineProfile.automaticGenerationDuringBuild = true
        }
        create("staging") {
            isMinifyEnabled = providers.gradleProperty("minifyWithR8")
                .map(String::toBooleanStrict).getOrElse(true)
            signingConfig = signingConfigs.getByName("release")
            applicationIdSuffix = ".stg"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    buildFeatures {
        buildConfig = true
    }
    testOptions.unitTests.isIncludeAndroidResources = true
    namespace = "com.swahilib"
}

dependencies {
//    implementation(projects.feature.interests.api)
//    implementation(projects.feature.interests.impl)
//    implementation(projects.feature.foryou.api)
//    implementation(projects.feature.foryou.impl)
//    implementation(projects.feature.bookmarks.api)
//    implementation(projects.feature.bookmarks.impl)
//    implementation(projects.feature.topic.api)
//    implementation(projects.feature.topic.impl)
//    implementation(projects.feature.search.api)
//    implementation(projects.feature.search.impl)
//    implementation(projects.feature.settings.impl)

//    implementation(projects.core.common)
//    implementation(projects.core.ui)
//    implementation(projects.core.designsystem)
//    implementation(projects.core.data)
//    implementation(projects.core.model)
//    implementation(projects.core.analytics)
//    implementation(projects.sync.work)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.icons.extended)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.lottie.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.compose.navigation)
    implementation(libs.compose.hilt.navigation)
    implementation(libs.androidx.compose.livedata)

    implementation(libs.android.billing)
    implementation(libs.revenuecat)
    implementation(libs.revenuecat.ui)

    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3.logging)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)
//    debugImplementation(projects.uiTestHiltManifest)

    kspTest(libs.hilt.compiler)
//
//    testImplementation(projects.core.dataTest)
//    testImplementation(projects.core.datastoreTest)
    testImplementation(libs.hilt.android.testing)
//    testImplementation(projects.sync.syncTest)
    testImplementation(libs.kotlin.test)
//    androidTestImplementation(projects.core.testing)
//    androidTestImplementation(projects.core.dataTest)
//    androidTestImplementation(projects.core.datastoreTest)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlin.test)
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}
