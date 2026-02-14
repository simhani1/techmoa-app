apply(plugin = "java-test-fixtures")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework:spring-tx")
    implementation("io.jsonwebtoken:jjwt:0.13.0")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    testRuntimeOnly("com.h2database:h2")
}
