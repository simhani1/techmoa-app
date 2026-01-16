apply(plugin = "java-test-fixtures")

dependencies {
    runtimeOnly(project(":app-infra:app-infra-db"))
    runtimeOnly(project(":app-infra:app-infra-oauth"))
    runtimeOnly(project(":app-infra:app-infra-jwt"))

    implementation("org.slf4j:slf4j-api")

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
}
