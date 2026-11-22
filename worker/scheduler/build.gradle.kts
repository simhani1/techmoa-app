dependencies {
    implementation(project(":domain"))
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly(project(":infrastructure:mysql"))
    compileOnly("org.springframework:spring-tx")
    compileOnly("org.springframework:spring-context")
}
