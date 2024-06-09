import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
}

group = "me.aeon"
version = "1.0.3"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.respark.dev/releases")
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("dev.dejvokep:boosted-yaml:1.3.5")
    compileOnly("net.kyori:adventure-text-minimessage") { version { strictly("4.11.0") } }
    implementation("dev.respark.licensegate:license-gate:1.0.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinJvmCompile>()
    .configureEach {
        compilerOptions.languageVersion
            .set(KotlinVersion.KOTLIN_2_0)
        compilerOptions.jvmTarget
            .set(JvmTarget.JVM_17)
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
