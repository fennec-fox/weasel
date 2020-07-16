group = "io.mustelidae.weasel"

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.3.0.RELEASE")
}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}

