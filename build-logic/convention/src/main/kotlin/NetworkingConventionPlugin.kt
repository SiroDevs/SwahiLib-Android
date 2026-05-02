import com.swahilib.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class NetworkingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "implementation"(libs.findLibrary("ktor.client.android").get())
                "implementation"(libs.findLibrary("squareup.retrofit").get())
                "implementation"(libs.findLibrary("squareup.retrofit.gson").get())
                "implementation"(libs.findLibrary("squareup.okhttp3.logging").get())
            }
        }
    }
}
