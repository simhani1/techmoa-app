apply(plugin = "java-test-fixtures")

dependencies {
    runtimeOnly(project(":app-infra:app-infra-db"))
    runtimeOnly(project(":app-infra:app-infra-oauth"))

    // Source: https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
    implementation("io.jsonwebtoken:jjwt:0.13.0")
    implementation("org.slf4j:slf4j-api")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.14.7")

    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
}
