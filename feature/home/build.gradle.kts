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
    implementation(project(":feature:library"))
    implementation(libs.androidx.foundation)

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
