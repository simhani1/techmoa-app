import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	kotlin("jvm") version "1.9.25" apply false
	kotlin("plugin.spring") version "1.9.25" apply false
	id("org.springframework.boot") version "3.5.9" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	kotlin("plugin.jpa") version "1.9.25" apply false
}

allprojects {
	group = "site"
	version = "0.0.1-SNAPSHOT"
	description = "TechMoa Application"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
	apply(plugin = "io.spring.dependency-management")

	extensions.configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
		imports {
			mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.9")
		}
	}

	dependencies {
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:1.1.15")
		testImplementation("org.junit.jupiter:junit-jupiter")
		testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
		testImplementation("io.kotest:kotest-assertions-core:5.9.1")
		testImplementation("io.mockk:mockk:1.14.7")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	extensions.configure<JavaPluginExtension> {
		toolchain {
			languageVersion = JavaLanguageVersion.of(17)
		}
	}

	tasks.withType<KotlinCompile>().configureEach {
		compilerOptions {
			freeCompilerArgs.add("-Xjsr305=strict")
		}
	}
}
