import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    id("maven-publish")
}

group = "me.aeon"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("dev.dejvokep:boosted-yaml:1.3.1")
    compileOnly("net.kyori:adventure-text-minimessage") { version { strictly("4.11.0") } }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
            artifactId = "aeons-commons-lib"
        }
    }
}
