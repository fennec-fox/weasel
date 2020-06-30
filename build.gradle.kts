import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


buildscript {
    repositories {
        mavenLocal()
        jcenter()
        maven("https://repo.spring.io/milestone")
        maven("https://palantir.bintray.com/releases/")
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
        classpath(kotlin("noarg", version = "1.3.72"))
        classpath(kotlin("allopen", version = "1.3.72"))
        classpath("gradle.plugin.com.avast.gradle:gradle-docker-compose-plugin:0.9.5")
    }
}

plugins {
    java
    idea
    base
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    kotlin("plugin.allopen") version "1.3.72"
    id("org.springframework.boot") version "2.3.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.jmailen.kotlinter") version "2.3.2"
    id("com.avast.gradle.docker-compose") version "0.12.1"
}

group = "io.mustelidae"

allprojects {
    version = "1.0-SNAPSHOT"

    // ref: https://kotlinlang.org/docs/reference/compiler-plugins.html
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("io.spring.dependency-management")
        plugin("org.springframework.boot")
        plugin("org.jmailen.kotlinter")
        plugin("kotlin-jpa")
        plugin("kotlin-spring")
    }

    repositories {
        mavenLocal()
        jcenter()
        maven("https://palantir.bintray.com/releases/")
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("reflect"))
        implementation(kotlin("stdlib-jdk8"))

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")

        implementation("ch.qos.logback:logback-classic")
        implementation("ch.qos.logback:logback-core")

        testImplementation("io.mockk:mockk:1.9.3")

        implementation("com.github.kittinunf.fuel:fuel:2.2.2")
        implementation("com.github.kittinunf.fuel:fuel-jackson:2.2.2")

        testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")

        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework:spring-context")
        implementation("org.slf4j:slf4j-api")
        implementation("org.slf4j:slf4j-log4j12")

        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter-engine")
    }
}

dependencies {

    implementation(project(":paygate"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    implementation("au.com.console:kotlin-jpa-specification-dsl:2.0.0-rc.1")

    kapt("com.querydsl:querydsl-apt:4.3.1:jpa")
    implementation("com.querydsl:querydsl-jpa:4.3.1")
    implementation("com.querydsl:querydsl-apt:4.3.1:jpa")

    implementation("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")

    runtimeOnly("mysql:mysql-connector-java:8.0.19")

    implementation("org.hibernate:hibernate-java8")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow") {
        exclude("io.undertow", "undertow-websockets-jsr")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "junit", module = "junit")
    }
    implementation("org.springframework.data:spring-data-envers")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
}

tasks.withType<Test> {
    useJUnitPlatform()

    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                val output =
                    "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
                val startItem = "|  "
                val endItem = "  |"
                val repeatLength = startItem.length + output.length + endItem.length
                println("\n${"-".repeat(repeatLength)}\n|  $output  |\n${"-".repeat(repeatLength)}")
            }
        }
    })
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

dockerCompose {
    // settings as usual
    createNested("infraSetting").apply {
        stopContainers = false
        useComposeFiles = listOf("docker-compose.yml")
    }
}
