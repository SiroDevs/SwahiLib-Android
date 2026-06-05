pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SwahiLib"

// App shell
include(":app")

// Core modules
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:designsystem")
include(":core:network")
include(":core:ui")

// Feature modules
include(":feature:splash")
include(":feature:home")
include(":feature:advsearch")
include(":feature:word")
include(":feature:idiom")
include(":feature:proverb")
include(":feature:saying")
include(":feature:settings")
include(":feature:howitworks")
include(":feature:help")
include(":feature:donation")
