pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal() // maven("https://plugins.gradle.org/m2")

        maven("https://repo.spring.io/milestone")

        // Snapshot von Spring Boot, ...
        //maven("https://repo.spring.io/snapshot") { mavenContent { snapshotsOnly() } }
        //maven("https://repo.spring.io/plugins-release")
    }
}

rootProject.name = "customer"
