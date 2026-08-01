plugins {
    alias(libs.plugins.swahilib.android.library)
    alias(libs.plugins.swahilib.hilt)
}

android {
    namespace = "com.swahilib.core.games"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:engagement"))
}
