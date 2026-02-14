plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.9"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
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

// 전체 서브모듈 공통: Kotlin + 테스트
val kotestVersion = "5.9.1"
val mockkVersion = "1.14.7"
val fixtureMonkeyVersion = "1.1.15"

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
	apply(plugin = "io.spring.dependency-management")

	dependencies {
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.slf4j:slf4j-api")

		testRuntimeOnly("org.junit.platform:junit-platform-launcher")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
		testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
		testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
		testImplementation("io.mockk:mockk:$mockkVersion")
		testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:$fixtureMonkeyVersion")
	}
}