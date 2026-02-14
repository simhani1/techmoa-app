plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    testRuntimeOnly("com.h2database:h2")

    implementation(project(":domain"))
    implementation(project(":presentation"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
    implementation(project(":infrastructure:oauth"))
    implementation(project(":infrastructure:mysql"))
    implementation(project(":infrastructure:jpa"))
    implementation(project(":infrastructure:jdbc"))
}
