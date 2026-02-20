dependencies {
    implementation(project(":infrastructure:jpa"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    runtimeOnly(project(":infrastructure:mysql"))
}
