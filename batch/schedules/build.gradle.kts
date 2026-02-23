dependencies {
    implementation(project(":domain"))
    runtimeOnly(project(":infrastructure:mysql"))

    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    compileOnly("org.springframework:spring-tx")
    compileOnly("org.springframework:spring-context")
}
