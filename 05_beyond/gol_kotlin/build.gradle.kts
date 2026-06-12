plugins {
    kotlin("jvm") version "2.1.0"
    application
}

repositories {
    mavenCentral()
}

application {
    // Top-level main() in Main.kt (package org.example) -> org.example.MainKt
    mainClass.set("org.example.MainKt")
}

kotlin {
    jvmToolchain(21)
}
