import com.swahilib.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for feature modules.
 * Applies: android library + compose + hilt.
 * Adds standard feature dependencies: core:ui, core:common.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("swahilib.android.library.compose")
            pluginManager.apply("swahilib.hilt")

            dependencies {
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:common"))
                "implementation"(project(":core:designsystem"))

                "implementation"(libs.findLibrary("androidx.compose.bom").get())
                "implementation"(libs.findLibrary("androidx.activity.compose").get())
                "implementation"(libs.findLibrary("androidx.ui").get())
                "implementation"(libs.findLibrary("androidx.ui.graphics").get())

                "implementation"(libs.findLibrary("androidx.material3").get())
                "implementation"(libs.findLibrary("androidx.compose.material").get())
                "implementation"(libs.findLibrary("androidx.icons.extended").get())

                "implementation"(libs.findLibrary("androidx.ui.tooling").get())
                "implementation"(libs.findLibrary("androidx.foundation").get())

                "implementation"(libs.findLibrary("compose.navigation").get())
                "implementation"(libs.findLibrary("compose.hilt.navigation").get())
                "implementation"(libs.findLibrary("androidx.compose.livedata").get())
            }
        }
    }
}
