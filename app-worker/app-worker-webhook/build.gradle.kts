dependencies {
    implementation(project(":app-domain"))
    implementation(project(":app-infra:app-infra-jdbc"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
