dependencies {
    implementation(project(":infrastructure:jpa"))
    runtimeOnly(project(":infrastructure:mysql"))

    compileOnly("org.springframework:spring-tx")
    compileOnly("org.springframework:spring-context")
}
