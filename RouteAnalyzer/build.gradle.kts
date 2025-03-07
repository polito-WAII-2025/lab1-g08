plugins {
    kotlin("jvm") version "2.1.10"
    application
    id("com.gradleup.shadow") version "8.3.5"
}

group = "it.polito.g08"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
application {
    mainClass.set("it.polito.g08.MainKt")
}