group = "io.mustelidae.weasel"

dependencies {}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}