plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.dailyword"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:database"))
}
