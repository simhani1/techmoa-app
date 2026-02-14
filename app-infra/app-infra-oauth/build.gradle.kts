dependencies {
    implementation(project(":app-core"))

    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-context")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
