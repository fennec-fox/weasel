group = "io.mustelidae.weasel"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.3.0.RELEASE")
}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}

