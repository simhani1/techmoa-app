val jwtVersion = "0.13.0"

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))

    // Source: https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
    implementation("io.jsonwebtoken:jjwt:${jwtVersion}")
}