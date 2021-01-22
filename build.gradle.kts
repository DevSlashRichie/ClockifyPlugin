import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")

    id("org.jetbrains.kotlin.jvm") version "1.4.21"

    id("org.jetbrains.intellij") version "0.6.5"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

group = "io.github.ricardormdev.clockifyplugin"
version = "1.0.0"

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}
listOf("compileKotlin", "compileTestKotlin").forEach {
    tasks.getByName<KotlinCompile>(it) {
        kotlinOptions.jvmTarget = "1.8"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
    implementation("com.konghq:unirest-java:3.11.09")
    implementation("com.auth0:java-jwt:3.12.0")
}

intellij {
    version = "2020.1"
    pluginName = "Clockify Plugin"
    updateSinceUntilBuild = false
}