plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.word"
}

dependencies {
    implementation(project(":core:data"))
}
