pluginManagement {
    includeBuild("build-conventions")
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

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PureAndroid"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":app",
    ":core:domain",
    ":core:resources",
    ":core:routing",
    ":core:ui",
    ":feature:main:domain",
    ":feature:main:routing",
    ":feature:main:ui",
    ":feature:map:routing",
    ":feature:map:ui",
    ":uikit",
)
