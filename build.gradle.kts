import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
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

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")

	dependencies {
		implementation("org.jetbrains.kotlin:kotlin-reflect")

		testRuntimeOnly("org.junit.platform:junit-platform-launcher")

		testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
		testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
		testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
		testImplementation("io.mockk:mockk:$mockkVersion")
	}
}

// Spring 모듈 (domain 제외): Spring Boot + Actuator + Prometheus
configure(subprojects.filter { it.name != "domain" }) {
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("org.springframework.boot:spring-boot-starter-actuator")

		runtimeOnly("io.micrometer:micrometer-registry-prometheus")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
	}

	// boot 모듈만 bootJar 활성화
	if (project.name != "boot") {
		tasks.getByName<BootJar>("bootJar") {
			enabled = false
		}
		tasks.getByName<Jar>("jar") {
			enabled = true
		}
	}
}

// Root project는 빌드 결과물 비활성화
tasks.getByName<BootJar>("bootJar") {
	enabled = false
}

tasks.getByName<Jar>("jar") {
	enabled = false
}
