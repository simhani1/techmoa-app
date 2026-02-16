pluginManagement {
    val kotlinVersion = "2.0.0"
    val springBootVersion = "3.5.9"
    val springDependencyManagementVersion = "1.1.7"

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
