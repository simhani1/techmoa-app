dependencies {
    implementation(project(":app-storage:app-storage-db"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-context-support")

    // https://mvnrepository.com/artifact/com.apptasticsoftware/rssreader
    implementation("com.apptasticsoftware:rssreader:3.12.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:1.1.15")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.14.7")
    testRuntimeOnly("com.h2database:h2")
}