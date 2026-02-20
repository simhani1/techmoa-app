plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":presentation"))
    implementation(project(":application"))
    implementation(project(":batch:rss"))
    implementation(project(":batch:schedules"))
    implementation(project(":infrastructure:oauth"))
    implementation(project(":infrastructure:mysql"))
    implementation(project(":infrastructure:jpa"))

    implementation("org.springframework.boot:spring-boot-starter")
    testRuntimeOnly("com.h2database:h2")
}
