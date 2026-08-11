plugins {
    alias(libs.plugins.swahilib.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.swahilib.feature.word_builder"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:games"))
    implementation(libs.kotlinx.serialization.json)
}
