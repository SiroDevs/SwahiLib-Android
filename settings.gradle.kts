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
include(":core:design_system")
include(":core:engagement")
include(":core:games")
include(":core:network")
include(":core:social")
include(":core:ui")

// Feature modules
include(":feature:home")
include(":feature:advanced_search")
include(":feature:word")
include(":feature:idiom")
include(":feature:proverb")
include(":feature:saying")
include(":feature:settings")
include(":feature:how_it_works")
include(":feature:help")
include(":feature:donation")
include(":feature:daily_content")
include(":feature:progress")
include(":feature:quiz")
include(":feature:word_builder")
include(":feature:sentence_builder")
include(":feature:spelling")
include(":feature:crossword")
include(":feature:word_search")
include(":feature:hangman")
