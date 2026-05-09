plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.splash"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
}
