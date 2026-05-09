plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.proverb"
}

dependencies {
    implementation(project(":core:data"))
}
