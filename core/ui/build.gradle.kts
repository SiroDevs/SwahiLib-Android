plugins {
    alias(libs.plugins.swahilib.android.library.compose)
    alias(libs.plugins.swahilib.hilt)
}

android {
    namespace = "com.swahilib.core.ui"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:design_system"))
    api(project(":core:engagement"))

    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.icons.extended)
}
