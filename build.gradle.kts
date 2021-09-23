import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.5.31"

	id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
	mavenCentral()

	maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
	implementation("org.bstats:bstats-bukkit:2.2.1")

	compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
	compileOnly("org.hjson:hjson:3.0.0")
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