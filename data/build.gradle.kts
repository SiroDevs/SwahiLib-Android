import java.util.Properties

plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.android.library.jacoco)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.swahilib.supabase)
    alias(libs.plugins.swahilib.android.room)
    id("kotlin-parcelize")
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.swahilib.data"
    testOptions.unitTests.isIncludeAndroidResources = true
    defaultConfig {
        buildConfigField("String", "SupabaseUrl", "\"${localProperties.getProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SupabaseKey", "\"${localProperties.getProperty("SUPABASE_ANON_KEY")}\"")
    }
}

android.buildFeatures.buildConfig = true
