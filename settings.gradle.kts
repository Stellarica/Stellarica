rootProject.name = "Stellarica"
pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()

		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		maven("https://papermc.io/repo/repository/maven-public/") {
			name = "Paper"
		}
	}
	@Suppress("LocalVariableName")
	plugins {
		val paperweight_version: String by settings
		val loom_version: String by settings
		val kotlin_version: String by settings
		val shadow_version: String by settings
		val detekt_version: String by settings

		id("fabric-loom") version loom_version
		id("io.papermc.paperweight.userdev") version paperweight_version
		id("com.github.johnrengelman.shadow") version shadow_version
		id("io.gitlab.arturbosch.detekt") version detekt_version
		kotlin("jvm") version kotlin_version
		kotlin("plugin.serialization") version kotlin_version
	}
}
include("client")
include("common")
include("server:paper")
include("server:mixin")