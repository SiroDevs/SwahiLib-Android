import com.swahilib.configureGraphTasks
import com.swahilib.configureSpotlessForRootProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.configuration.BuildFeatures
import javax.inject.Inject

abstract class RootPlugin : Plugin<Project> {
    @get:Inject abstract val buildFeatures: BuildFeatures

    override fun apply(target: Project) {
        require(target.path == ":")
        if (!buildFeatures.isIsolatedProjectsEnabled()) {
            target.subprojects { configureGraphTasks() }
        }
        target.configureSpotlessForRootProject()
    }
}

private fun BuildFeatures.isIsolatedProjectsEnabled(): Boolean {
    return isolatedProjects.active.getOrElse(false)
}
