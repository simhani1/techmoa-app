dependencies {
    implementation(project(":app-storage:app-storage-db"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-context-support")

    // https://mvnrepository.com/artifact/com.apptasticsoftware/rssreader
    implementation("com.apptasticsoftware:rssreader:3.12.0")
}