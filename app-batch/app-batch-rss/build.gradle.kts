dependencies {
    implementation(project(":app-core"))
    testImplementation(testFixtures(project(":app-core")))

    // https://mvnrepository.com/artifact/com.apptasticsoftware/rssreader
    implementation("com.apptasticsoftware:rssreader:3.12.0")
    implementation("org.slf4j:slf4j-api")

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
}