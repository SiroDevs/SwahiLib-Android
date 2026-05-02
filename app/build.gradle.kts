import java.util.Properties

plugins {
    alias(libs.plugins.swahilib.android.app)
    alias(libs.plugins.swahilib.android.app.compose)
    alias(libs.plugins.swahilib.android.app.jacoco)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.swahilib.networking)
    alias(libs.plugins.swahilib.subscriptions)
    alias(libs.plugins.swahilib.supabase)
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
    defaultConfig {
        applicationId = "com.swahilib"
        versionCode = 150
        versionName = "1.0.150"
        testInstrumentationRunner = "com.swahilib.testing.AppTestRunner"

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

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}


sentry {
    org.set("futuristicken")
    projectName.set("swahilib-android")
    includeSourceContext.set(true)
}

dependencies {
    api(project(":data"))
    api(project(":domain"))
    api(project(":presentation"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.kotlin.test)

    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlin.test)
}
