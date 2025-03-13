plugins {
    kotlin("jvm") version "2.1.10"
    application
    id("com.gradleup.shadow") version "8.3.5"
    id("com.google.devtools.ksp") version "2.1.10-1.0.31"
    id("org.jetbrains.kotlinx.dataframe") version "0.15.0-RC3"
}

group = "it.polito.g08"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.jetbrains.kotlinx:dataframe:0.15.0-RC3")
    implementation("org.slf4j:slf4j-nop:2.0.17")
    implementation("org.yaml:snakeyaml:1.30")//aggiunto Alice per leggere i file yaml
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "it.polito.g08.MainKt"
}