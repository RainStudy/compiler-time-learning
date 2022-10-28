import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10"
    `java-gradle-plugin`
}

group = "org.example"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven { url = uri("https://www.jitpack.io") }
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("methodPrefPlugin") {
            id = "team.redrock.rain"
            implementationClass = "team.redrock.rain.kcp.MethodPrefIRPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.7.10")
    implementation("com.google.auto.service:auto-service:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")

    testImplementation(kotlin("test"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}