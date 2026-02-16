pluginManagement {
    val kotlinVersion = providers.gradleProperty("kotlinVersion").get()
    val springBootVersion = providers.gradleProperty("springBootVersion").get()
    val springDependencyManagementVersion = providers.gradleProperty("springDependencyManagementVersion").get()
    val sonarQubePluginVersion = providers.gradleProperty("sonarQubePluginVersion").get()

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("org.sonarqube") version sonarQubePluginVersion
    }
}

rootProject.name = "app"

include("boot")
include("presentation")
include("domain")
include("application")

include("infrastructure")
include("infrastructure:mysql")
include("infrastructure:jpa")
include("infrastructure:oauth")

include("batch")
include("batch:rss")
