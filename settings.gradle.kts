pluginManagement {
    repositories {
/*        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()*/
        maven {
            setUrl("https://maven.myket.ir")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
/*        google()
        mavenCentral()*/
        maven {
            setUrl("https://maven.myket.ir")
        }
    }
}

rootProject.name = "samplePlayer"
include(":app")
 