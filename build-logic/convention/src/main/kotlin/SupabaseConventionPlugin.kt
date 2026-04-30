import com.swahilib.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SupabaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "implementation"(platform(libs.findLibrary("supabase.bom").get()))
                "implementation"(libs.findLibrary("supabase.postgrest").get())
            }
        }
    }
}
