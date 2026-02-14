pluginManagement {
	val kotlinVersion = "1.9.25"
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

include("app-api")
include("app-boot")
include("app-domain")

include("app-core")

include("app-batch:app-batch-rss")
include("app-worker")
include("app-worker:app-worker-webhook")

include("app-infra")
include("app-infra:app-infra-db")
include("app-infra:app-infra-oauth")
include("app-infra:app-infra-jdbc")
include("app-infra:app-infra-jpa")
