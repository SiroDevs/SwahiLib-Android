plugins {
    alias(libs.plugins.swahilib.android.feature)
    alias(libs.plugins.swahilib.android.library.compose)
}

android {
    namespace = "com.swahilib.feature.advanced_search"
}

dependencies {
    implementation(project(":feature:home"))
}
