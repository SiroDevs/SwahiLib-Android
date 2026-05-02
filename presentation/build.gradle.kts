plugins {
    alias(libs.plugins.swahilib.android.app)
    alias(libs.plugins.swahilib.android.app.compose)
    alias(libs.plugins.swahilib.android.app.jacoco)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.swahilib.supabase)
}

android {
    namespace = "com.swahilib.presentation"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(project(":core"))
    api(project(":data"))
    api(project(":domain"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.icons.extended)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.lottie.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.compose.navigation)
    implementation(libs.compose.hilt.navigation)
    implementation(libs.androidx.compose.livedata)
}