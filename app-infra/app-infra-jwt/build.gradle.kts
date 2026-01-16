dependencies {
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-context")

    implementation("org.slf4j:slf4j-api")

    // Source: https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
    api("io.jsonwebtoken:jjwt:0.13.0")
}
