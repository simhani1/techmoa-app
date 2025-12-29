dependencies {
    implementation(project(":app-core"))
    implementation(project(":app-storage:app-storage-db"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-context-support")

    // https://mvnrepository.com/artifact/com.rometools/rome
    implementation("com.rometools:rome:2.1.0")
}