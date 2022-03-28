import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.10"

	id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
	mavenCentral()

	maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
	implementation("org.bstats:bstats-bukkit:3.0.0")

	compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

tasks.withType<KotlinCompile>().configureEach {
	kotlinOptions {
		jvmTarget = "16"
	}
}

tasks.shadowJar {
	relocate("org.bstats", "io.github.petercrawley.minecraftstarshipplugin")

	minimize()
}