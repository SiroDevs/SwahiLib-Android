plugins {
    alias(libs.plugins.swahilib.android.feature)
    alias(libs.plugins.swahilib.android.library.compose)
}

android {
    namespace = "com.swahilib.feature.auth"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:social"))
    implementation(libs.androidx.foundation)
}
