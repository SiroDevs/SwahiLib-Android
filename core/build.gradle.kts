plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.android.library.jacoco)
    alias(libs.plugins.swahilib.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.swahilib.core"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {

}