plugins {
    alias(libs.plugins.swahilib.android.feature)
    alias(libs.plugins.swahilib.android.library.compose)
}

android {
    namespace = "com.swahilib.feature.social"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:social"))
    implementation(project(":core:engagement"))

    implementation(libs.androidx.foundation)
}
