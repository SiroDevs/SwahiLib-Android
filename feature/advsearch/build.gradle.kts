plugins {
    alias(libs.plugins.swahilib.android.feature)
    alias(libs.plugins.swahilib.android.library.compose)
}

android {
    namespace = "com.swahilib.feature.advsearch"
}

dependencies {
    implementation(project(":feature:home"))
}
