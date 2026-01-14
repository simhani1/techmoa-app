apply(plugin = "java-test-fixtures")

dependencies {
    runtimeOnly(project(":app-infra:app-infra-db"))

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
}
