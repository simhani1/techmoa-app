dependencies {
    implementation(project(":domain"))

    compileOnly("org.springframework:spring-tx")
    compileOnly("org.springframework:spring-context")
}