import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")

            dependencies {
                add("implementation", "com.google.dagger:hilt-android:2.57.2")
                add("ksp", "com.google.dagger:hilt-compiler:2.57.2")
            }
        }
    }
}
