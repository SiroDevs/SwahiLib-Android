plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.swahilib.core.games"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:engagement"))
    implementation(libs.kotlinx.serialization.json)
}
