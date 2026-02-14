plugins {
    kotlin("plugin.jpa")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure:mysql"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
