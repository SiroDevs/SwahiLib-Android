plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.hilt)
}

android {
    namespace = "com.swahilib.core.data"
}

dependencies {
    api(project(":core:database"))
    api(project(":core:network"))
    implementation(project(":core:common"))

    api(libs.androidx.compose.material)

    implementation(platform(libs.jan.tennert.supabase.bom))
    implementation(libs.jan.tennert.supabase.postgrest)
    implementation(libs.androidx.core.ktx)
    implementation(libs.revenuecat)
}
