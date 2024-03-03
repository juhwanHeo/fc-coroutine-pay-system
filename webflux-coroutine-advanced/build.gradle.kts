import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

group = "com.fastcampus"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	// https://velog.io/@kshired/MacOS-M1-Chip-Spring-Cloud-Gateway-%EC%97%90%EB%9F%AC-%ED%95%B4%EA%B2%B0%EB%B2%95
	implementation("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")

	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.4")

	testImplementation("org.testcontainers:testcontainers:1.19.0")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

	implementation("io.github.resilience4j:resilience4j-kotlin:2.1.0")
	implementation("io.github.resilience4j:resilience4j-all:2.1.0")

	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("io.micrometer:context-propagation:1.0.5")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.7.3")

	implementation("io.github.microutils:kotlin-logging:3.0.5")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	runtimeOnly("org.mariadb:r2dbc-mariadb:1.1.3")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("io.r2dbc:r2dbc-h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("io.kotest:kotest-runner-junit5:5.6.1")
	testImplementation("io.kotest:kotest-assertions-core:5.6.1")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
