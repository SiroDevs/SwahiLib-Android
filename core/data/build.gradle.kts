import java.util.Properties

plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.hilt)
}

android {
    namespace = "com.swahilib.core.data"
    buildFeatures { buildConfig = true }

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    defaultConfig {
        buildConfigField("String", "PaystackSecret", "\"${localProperties.getProperty("PAYSTACK_SECRET_KEY") ?: ""}\"")
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:engagement"))
    api(project(":core:network"))
    api(project(":core:games"))

    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
