plugins {
    alias(libs.plugins.swahilib.android.feature)
    alias(libs.plugins.swahilib.android.library.compose)
}

android {
    namespace = "com.swahilib.feature.likes"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(libs.androidx.foundation)
}
