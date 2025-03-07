plugins {
    kotlin("jvm") version "2.1.10"
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