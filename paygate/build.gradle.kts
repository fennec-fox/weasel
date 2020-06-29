group = "io.mustelidae.weasel"

dependencies {
    implementation(project(":security"))
    implementation("com.google.guava:guava:29.0-jre")
}

sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
}
