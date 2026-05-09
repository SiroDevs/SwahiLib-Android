import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<LibraryExtension> {
                compileSdk = 35
                defaultConfig {
                    minSdk = 26
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                compileOptions {
                    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
                    targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
                }
                kotlinOptions { jvmTarget = "11" }
            }
        }
    }

    // Extension function kept here for brevity; in real projects extract to a separate file
    private fun LibraryExtension.kotlinOptions(block: org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions.() -> Unit) {
        (this as org.gradle.api.plugins.ExtensionAware).extensions
            .configure("kotlinOptions", block)
    }
}
