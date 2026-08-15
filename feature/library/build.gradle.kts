plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.library"
}

dependencies {
    implementation(project(":core:data"))
}
