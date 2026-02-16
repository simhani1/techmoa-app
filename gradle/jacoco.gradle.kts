import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport

dependencies {
    subprojects.forEach { add("jacocoAggregation", it) }
}

subprojects {
    apply(plugin = "jacoco")
}

tasks.named<JacocoReport>("testCodeCoverageReport") {
    dependsOn(subprojects.map { it.tasks.named("test") })
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.named<Test>("test") {
    finalizedBy(tasks.named("testCodeCoverageReport"))
}
