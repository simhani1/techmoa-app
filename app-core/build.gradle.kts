apply(plugin = "java-test-fixtures")

dependencies {
    runtimeOnly(project(":app-infra:app-infra-db"))
    runtimeOnly(project(":app-infra:app-infra-oauth"))

    // Source: https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
    implementation("io.jsonwebtoken:jjwt:0.13.0")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

//    compileOnly("org.springframework.boot:spring-boot")
//    compileOnly("org.springframework:spring-context")
//    compileOnly("org.springframework:spring-tx")
}
