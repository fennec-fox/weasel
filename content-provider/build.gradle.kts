group = "io.mustelidae.weasel"

dependencies {
    implementation(project(":common"))
}


tasks.getByName<Jar>("bootJar") {
    enabled = false
}