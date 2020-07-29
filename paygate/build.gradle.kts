group = "io.mustelidae.weasel"

dependencies {
    implementation(project(":security"))
    implementation(project(":common"))
    implementation("com.google.guava:guava:29.0-jre")

    kapt("com.querydsl:querydsl-apt:4.3.1:jpa")
    implementation("com.querydsl:querydsl-jpa:4.3.1")
    implementation("com.querydsl:querydsl-apt:4.3.1:jpa")
}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}