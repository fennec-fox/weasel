group = "io.mustelidae.weasel"

dependencies {
    implementation(project(":security"))
    implementation(project(":common"))
    implementation("com.google.guava:guava:29.0-jre")
}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}