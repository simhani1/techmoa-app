import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
	kotlin("jvm") apply false
	kotlin("plugin.spring") apply false
	id("org.springframework.boot") apply false
	id("io.spring.dependency-management") apply false
	kotlin("plugin.jpa") apply false
}

allprojects {
	group = "site"
	version = "0.0.1-SNAPSHOT"
	description = "TechMoa Application"

	repositories {
		mavenCentral()
	}

	tasks.withType<Test> {
		useJUnitPlatform()

		testLogging {
			showStandardStreams = true
			events("passed", "skipped", "failed")

			debug {
				events("standardOut", "standardError")
				exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
			}
		}
	}
}

val kotestVersion = "5.9.1"
val mockkVersion = "1.14.7"
val fixtureMonkeyVersion = "1.1.15"

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "io.spring.dependency-management")

	if (path != ":app-domain") {
		apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	}

	if (path == ":app-infra:app-infra-db" || path == ":app-infra:app-infra-jpa") {
		apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
	}

	extensions.configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
		imports {
			mavenBom(SpringBootPlugin.BOM_COORDINATES)
		}
	}

	pluginManager.withPlugin("org.springframework.boot") {
		if (name != "app-api") {
			tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
				enabled = false
			}
			tasks.named<org.gradle.jvm.tasks.Jar>("jar") {
				enabled = true
			}
		}
	}

	dependencies {
		if (path != ":app-domain") {
			add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
			add("implementation", "org.slf4j:slf4j-api")
		}

		add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")

		if (path != ":app-domain") {
			add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
		}
		add("testImplementation", "org.jetbrains.kotlin:kotlin-test-junit5")
		add("testImplementation", "io.kotest:kotest-runner-junit5:$kotestVersion")
		add("testImplementation", "io.kotest:kotest-assertions-core:$kotestVersion")
		add("testImplementation", "io.mockk:mockk:$mockkVersion")
		add("testImplementation", "com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:$fixtureMonkeyVersion")
	}
}
