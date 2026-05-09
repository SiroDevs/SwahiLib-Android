plugins {
    alias(libs.plugins.swahilib.android.library.compose)
}

android {
    namespace = "com.swahilib.core.designsystem"
}

dependencies {
    api(project(":core:data"))


    api(libs.material3)
    api(libs.androidx.material3)
    api(libs.androidx.ui)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.foundation)
    api(libs.androidx.activity.compose)
    api(libs.androidx.compose.material)
    api(libs.androidx.icons.extended)

    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.ui.tooling.preview)
}
