group = "io.mustelidae.weasel"

dependencies {}

configurations.forEach {
    it.exclude("org.springframework")
    it.exclude("org.springframework.boot")
    it.exclude("ch.qos.logback")
    it.exclude("org.slf4j")
}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}