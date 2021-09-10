plugins {
	kotlin("jvm") version "1.5.21"

	id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
	mavenCentral()

	maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
	compileOnly("org.hjson:hjson:3.0.0")
}