val rssreaderVersion = "3.12.0"

dependencies {
    implementation(project(":app-api"))
    testImplementation(testFixtures(project(":app-api")))

    // https://mvnrepository.com/artifact/com.apptasticsoftware/rssreader
    implementation("com.apptasticsoftware:rssreader:$rssreaderVersion")

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
}
