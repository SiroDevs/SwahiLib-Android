plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.hilt)
    kotlin("plugin.serialization") version "2.1.21"
}

android {
    namespace = "com.swahilib.core.network"
    buildFeatures { buildConfig = true }
}

dependencies {
    api(project(":core:database"))

    implementation(platform(libs.jan.tennert.supabase.bom))
    implementation(libs.jan.tennert.supabase.postgrest)
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.serialization.json)
}
