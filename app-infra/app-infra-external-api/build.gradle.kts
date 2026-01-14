dependencies {
    implementation(project(":app-core"))
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:1.1.15")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
