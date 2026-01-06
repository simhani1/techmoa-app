dependencies {
    runtimeOnly(project(":app-storage:app-storage-db"))

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
}