apply(plugin = "java-test-fixtures")

dependencies {
    runtimeOnly(project(":app-infra:app-infra-db"))
    runtimeOnly(project(":app-infra:app-infra-external-api"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
}
