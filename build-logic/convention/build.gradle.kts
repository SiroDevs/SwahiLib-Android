import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.lint)
}

group = "com.swahilib.buildlogic"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    implementation(libs.truth)
    lintChecks(libs.androidx.lint.gradle)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("appCompose") {
            id = libs.plugins.swahilib.android.app.compose.get().pluginId
            implementationClass = "AndroidAppComposeConventionPlugin"
        }
        register("androidApp") {
            id = libs.plugins.swahilib.android.app.asProvider().get().pluginId
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("jacoco") {
            id = libs.plugins.swahilib.android.app.jacoco.get().pluginId
            implementationClass = "AndroidAppJacocoConventionPlugin"
        }
        register("composeLibrary") {
            id = libs.plugins.swahilib.android.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.swahilib.android.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("featureImpl") {
            id = libs.plugins.swahilib.android.feature.impl.get().pluginId
            implementationClass = "AndroidFeatureImplConventionPlugin"
        }
        register("featureApi") {
            id = libs.plugins.swahilib.android.feature.api.get().pluginId
            implementationClass = "AndroidFeatureApiConventionPlugin"
        }
        register("jacocoLibrary") {
            id = libs.plugins.swahilib.android.library.jacoco.get().pluginId
            implementationClass = "AndroidLibraryJacocoConventionPlugin"
        }
        register("androidTest") {
            id = libs.plugins.swahilib.android.test.get().pluginId
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.swahilib.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("room") {
            id = libs.plugins.swahilib.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidLint") {
            id = libs.plugins.swahilib.android.lint.get().pluginId
            implementationClass = "AndroidLintConventionPlugin"
        }
        register("supabase") {
            id = libs.plugins.swahilib.supabase.get().pluginId
            implementationClass = "SupabaseConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.swahilib.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("root") {
            id = libs.plugins.swahilib.root.get().pluginId
            implementationClass = "RootPlugin"
        }
    }
}
