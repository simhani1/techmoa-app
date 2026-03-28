val rssreaderVersion = "3.12.0"

dependencies {
    runtimeOnly(project(":infrastructure:mysql"))

    // https://mvnrepository.com/artifact/com.apptasticsoftware/rssreader
    implementation("com.apptasticsoftware:rssreader:$rssreaderVersion")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
}