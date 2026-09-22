pluginManagement {
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

rootProject.name = "Krithi"
include(":app")

try {
    val cl = Class.forName("java.lang.ProcessEnvironment")
    val field = cl.getDeclaredField("theCaseInsensitiveEnvironment")
    field.isAccessible = true
    val map = field.get(null) as MutableMap<String, String>
    map.remove("ANDROID_PREFS_ROOT")
    
    val field2 = cl.getDeclaredField("theUnmodifiableEnvironment")
    field2.isAccessible = true
    val map2 = field2.get(null) as MutableMap<String, String>
    map2.remove("ANDROID_PREFS_ROOT")
} catch (e: Exception) {}
