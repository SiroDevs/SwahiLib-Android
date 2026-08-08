plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.word_builder"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:games"))
}
