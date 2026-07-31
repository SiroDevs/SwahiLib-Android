plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.hilt)
}

android {
    namespace = "com.swahilib.core.data"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:engagement"))
    api(project(":core:network"))

    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
