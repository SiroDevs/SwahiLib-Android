plugins {
    alias(libs.plugins.swahilib.android.app)
    alias(libs.plugins.swahilib.android.app.compose)
    alias(libs.plugins.swahilib.android.app.jacoco)
    alias(libs.plugins.swahilib.networking)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.swahilib.subscriptions)
}

android {
    namespace = "com.swahilib.core"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.kotlin.test)
}