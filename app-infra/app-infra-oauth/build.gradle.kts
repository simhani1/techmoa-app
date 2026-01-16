dependencies {
    implementation(project(":app-core"))
    implementation(project(":app-infra:app-infra-jwt"))

    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-context")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.slf4j:slf4j-api")

    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:1.1.15")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
