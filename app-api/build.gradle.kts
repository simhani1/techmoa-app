plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":app-batch:app-batch-rss"))
    implementation(project(":app-storage:app-storage-db"))

	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}

