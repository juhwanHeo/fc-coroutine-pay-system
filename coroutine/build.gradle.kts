plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.fastcampus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation("io.projectreactor:reactor-core:3.5.7")

    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}