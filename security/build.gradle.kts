group = "io.mustelidae.weasel"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis:2.3.0.RELEASE")
    testImplementation("it.ozimov:embedded-redis:0.7.3")
    testImplementation("org.testcontainers:testcontainers:1.14.3")
    testImplementation("org.testcontainers:junit-jupiter:1.14.3")

}


tasks.getByName<Jar>("bootJar") {
    enabled = false
}