plugins {
    `java-library`
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.23"
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.4.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.google.guava:guava:33.1.0-jre")
    implementation("com.miglayout:miglayout-swing:11.3")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}