plugins {
	kotlin("jvm") version "1.5.31"

	id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
	mavenCentral()

	maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
	compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
	compileOnly("org.hjson:hjson:3.0.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	kotlinOptions {
		jvmTarget = "16"
	}
}