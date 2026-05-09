plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.init"
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.squareup.retrofit)
}
