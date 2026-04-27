import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.swahilib.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        JvmTarget.JVM_17
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
//    lintChecks(libs.androidx.lint.gradle)
}

gradlePlugin {
    plugins {
        register("convention") {
            id = libs.plugins.swahilib.convention.get().pluginId
            implementationClass = "ConventionPlugin"
        }
        register("appCompose") {
            id = libs.plugins.swahilib.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("jacoco") {
            id = libs.plugins.swahilib.jacoco.get().pluginId
            implementationClass = "JacocoConventionPlugin"
        }
        register("libCompose") {
            id = libs.plugins.swahilib.lib.compose.get().pluginId
            implementationClass = "LibComposeConventionPlugin"
        }
        register("appLib") {
            id = libs.plugins.swahilib.lib.asProvider().get().pluginId
            implementationClass = "LibConventionPlugin"
        }
        register("featureImpl") {
            id = libs.plugins.swahilib.feature.impl.get().pluginId
            implementationClass = "FeatureImplConventionPlugin"
        }
        register("featureApi") {
            id = libs.plugins.swahilib.feature.api.get().pluginId
            implementationClass = "FeatureApiConventionPlugin"
        }
        register("libJacoco") {
            id = libs.plugins.swahilib.lib.jacoco.get().pluginId
            implementationClass = "LibJacocoConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.swahilib.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("room") {
            id = libs.plugins.swahilib.room.get().pluginId
            implementationClass = "RoomConventionPlugin"
        }
        register("appFlavors") {
            id = libs.plugins.swahilib.flavors.get().pluginId
            implementationClass = "AppFlavorsConventionPlugin"
        }
        register("lint") {
            id = libs.plugins.swahilib.lint.get().pluginId
            implementationClass = "LintConventionPlugin"
        }
        register("jvmLib") {
            id = libs.plugins.swahilib.jvm.lib.get().pluginId
            implementationClass = "JvmLibConventionPlugin"
        }
        register("root") {
            id = libs.plugins.swahilib.root.get().pluginId
            implementationClass = "RootPlugin"
        }
        register("test") {
            id = libs.plugins.swahilib.test.get().pluginId
            implementationClass = "TestConventionPlugin"
        }
    }
}
