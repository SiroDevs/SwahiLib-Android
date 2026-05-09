plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.saying"
}

dependencies {
    implementation(project(":core:data"))
}
