plugins {
    alias(libs.plugins.swahilib.android.feature)
}

android {
    namespace = "com.swahilib.feature.dailies"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":feature:word"))
    implementation(project(":feature:proverb"))
}
