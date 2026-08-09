import java.util.Properties

plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.swahilib.core.social"
    buildFeatures { buildConfig = true }

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    defaultConfig {
        buildConfigField("String", "SupabaseKey", "\"${localProperties.getProperty("SUPABASE_URL") ?: ""}\"")
        buildConfigField("String", "SupabaseAnonKey", "\"${localProperties.getProperty("SUPABASE_ANON_KEY") ?: ""}\"")
        buildConfigField("String", "GoogleWebClientId", "\"${localProperties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: ""}\"")
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:data"))

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.google.id)

    // Firebase Auth - see client/SupabaseClient.kt for why this replaced Supabase's own Auth
    // module. Aliases confirmed against app/build.gradle.kts, which already wires these.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    // Bridges FirebaseAuth's Task<T> APIs (e.g. getIdToken()) to suspend/.await() - not
    // referenced elsewhere in the project's catalog yet, so pinned directly here for now.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
