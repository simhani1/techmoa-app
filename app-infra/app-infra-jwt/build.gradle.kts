dependencies {
    implementation(project(":app-core"))
    implementation("org.slf4j:slf4j-api")

    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-context")

    // Source: https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
    api("io.jsonwebtoken:jjwt:0.13.0")
}
