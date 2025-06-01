// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false     // Android application plugin
    alias(libs.plugins.kotlin.android) apply false      // Kotlin support for Android
    alias(libs.plugins.kotlin.compose) apply false      // Jetpack Compose compiler plugin
    alias(libs.plugins.dagger.hilt) apply false     // Hilt for dependency injection
    alias(libs.plugins.ksp) apply false     // Kotlin Symbol Processing (used by Room, Hilt, etc.)
}