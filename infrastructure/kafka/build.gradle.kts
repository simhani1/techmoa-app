dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure:rest"))
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}
