import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.devtools.ksp)
}

val configProperties = Properties()
val configFile = rootProject.file("gradle/config/config.properties")
if (configFile.exists()) {
    configProperties.load(configFile.inputStream())
}

android {
    namespace = (configProperties["applicationId"] as String) + ".core"
    compileSdk = (configProperties["targetSdk"] as String).toInt()
}

dependencies {
    implementation(project(":app"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    kspAndroidTest(libs.hilt.android.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(platform(libs.jan.tennert.supabase.bom))
    implementation(libs.jan.tennert.supabase.postgrest)
    implementation(libs.ktor.client.android)
}