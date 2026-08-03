plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.hilt)
}

android {
    namespace = "com.swahilib.core.engagement"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
}
