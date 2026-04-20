import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.devtools.ksp)
}

val configProperties = Properties()
val configFile = rootProject.file("gradle/config/config.properties")
if (configFile.exists()) {
    configProperties.load(configFile.inputStream())
}

android {
    namespace = (configProperties["applicationId"] as String) + ".domain"
    compileSdk = (configProperties["targetSdk"] as String).toInt()
}

dependencies {
    implementation(project(":app"))
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":presentation"))

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.jan.tennert.supabase.bom))
    implementation(libs.jan.tennert.supabase.postgrest)
    implementation(libs.revenuecat)
}