plugins {
    alias(libs.plugins.swahilib.android.feature)
    alias(libs.plugins.swahilib.android.library.compose)
}

android {
    namespace = "com.swahilib.feature.home"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(libs.revenuecat)
    implementation(libs.revenuecat.ui)
}
