dependencies {
    implementation(project(":app-core"))
    runtimeOnly(project(":app-batch:app-batch-rss"))

	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testRuntimeOnly("com.h2database:h2")
}