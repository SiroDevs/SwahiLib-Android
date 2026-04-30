import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.swahilib.configureGradleManagedDevices
import com.swahilib.configureKotlinAndroid
import com.swahilib.configurePrintApksTask
import com.swahilib.configureSpotlessForAndroid
import com.swahilib.disableUnnecessaryAndroidTests
import com.swahilib.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

abstract class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "swahilib.android.lint")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                testOptions.targetSdk = 36
                lint.targetSdk = 36
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                configurePrintApksTask(this)
                disableUnnecessaryAndroidTests(target)
            }
            configureSpotlessForAndroid()
            dependencies {
                "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
                "testImplementation"(libs.findLibrary("kotlin.test").get())
                "testImplementation"(libs.findLibrary("junit").get())

                "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
            }
        }
    }
}
