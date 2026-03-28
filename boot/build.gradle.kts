plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":presentation"))
    implementation(project(":application"))

    implementation(project(":worker:rss"))
    implementation(project(":worker:scheduler"))

    implementation(project(":infrastructure:oauth"))
    implementation(project(":infrastructure:jpa"))
    implementation(project(":infrastructure:rest"))
    implementation(project(":infrastructure:kafka"))
    implementation(project(":infrastructure:mysql"))

    implementation("org.springframework.boot:spring-boot-starter")
    testRuntimeOnly("com.h2database:h2")
}
