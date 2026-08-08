plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.spelling"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:games"))
}
