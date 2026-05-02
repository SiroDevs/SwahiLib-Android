import com.swahilib.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SubscriptionsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "implementation"(libs.findLibrary("android.billing").get())
                "implementation"(libs.findLibrary("revenuecat.purchases").get())
                "implementation"(libs.findLibrary("revenuecat.purchases.ui").get())
            }
        }
    }
}
