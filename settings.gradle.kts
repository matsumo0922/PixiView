@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://www.jitpack.io")
        maven(url = "https://chaquo.com/maven-test")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://www.jitpack.io")
    }
}

rootProject.name = "PixiView"

include(":app")
include(":core:ui")
include(":core:repository")
include(":core:datastore")
include(":core:model")
include(":core:common")
include(":core:billing")
include(":feature:library")
include(":feature:report")
include(":feature:welcome")
include(":feature:post")
include(":feature:creator")
include(":feature:about")
include(":feature:setting")
