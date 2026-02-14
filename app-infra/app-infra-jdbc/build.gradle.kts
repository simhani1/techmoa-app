dependencies {
    implementation(project(":app-domain"))

    implementation("org.springframework:spring-jdbc")
    runtimeOnly("com.mysql:mysql-connector-j")
}
