plugins {
    alias(libs.plugins.swahilib.android.library.compose)
}

android {
    namespace = "com.swahilib.core.ui"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:design_system"))

    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.ui.tooling.preview)
}
