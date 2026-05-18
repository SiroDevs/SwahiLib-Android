import com.android.build.api.dsl.LibraryExtension
import com.swahilib.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Applies the base Android library plugin PLUS Compose support.
 * Feature modules that expose Composables should use this plugin.
 */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("swahilib.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            dependencies {
                "implementation"(libs.findLibrary("androidx.core.ktx").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtime.ktx").get())
            }
            extensions.configure<LibraryExtension> {
                buildFeatures { compose = true }
            }
        }
    }
}
