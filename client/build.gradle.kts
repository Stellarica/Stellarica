plugins {
	java
	kotlin("jvm")
	id("fabric-loom")
}
version = property("mod_version")!!
repositories {
	maven(uri("https://repo.virtualclient.gg/artifactory/virtualclient-public/"))
}

dependencies {
	implementation(project(":common", "namedElements"))
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings(loom.layered() {
		officialMojangMappings()
		"net.fabricmc:yarn:${property("yarn_mappings")}:v2"
	})

	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

	modImplementation(include("gg.virtualclient:virtualgui:${property("elementa_version")}")!!)
	implementation("io.github.microutils:kotlin-logging-jvm:${property("kt_log_version")}")
}

tasks {
	processResources {
		inputs.property("version", version)
		filesMatching("fabric.mod.json") {
			expand(mutableMapOf("version" to version))
		}
	}
}

java {
	withSourcesJar()
}
