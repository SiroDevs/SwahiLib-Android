plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.android.library.jacoco)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.swahilib.supabase)
    alias(libs.plugins.swahilib.subscriptions)
}

android {
    namespace = "com.swahilib.domain"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(project(":core"))
    api(project(":data"))
}