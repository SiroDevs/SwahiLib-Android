plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.progress"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:engagement"))
    implementation(libs.androidx.foundation)
}
