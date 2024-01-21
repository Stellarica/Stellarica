plugins {
	java
	id("io.papermc.paperweight.userdev")
	id("com.github.johnrengelman.shadow")
	kotlin("plugin.serialization")
}

repositories {
	maven("https://repo.mineinabyss.com/releases") // ProtocolBurrito
	maven("https://repo.aikar.co/content/groups/aikar/") // acf-paper
	maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
	maven("https://repo.stellarica.net/snapshots")
	mavenCentral()
}


dependencies {
	implementation(project(":common", "namedElements"))

	paperweightDevelopmentBundle("net.stellarica.nebula:dev-bundle:${property("minecraft_version")}-R0.1-SNAPSHOT")
	compileOnly("net.stellarica.nebula:nebula-api:${property("minecraft_version")}-R0.1-SNAPSHOT")

	implementation("io.github.microutils:kotlin-logging-jvm:${property("kt_log_version")}")

	listOf(
		"cloud-core",
		"cloud-paper",
		"cloud-kotlin-extensions",
		"cloud-kotlin-coroutines",
		"cloud-kotlin-coroutines-annotations",
		"cloud-annotations"
	).forEach { implementation("cloud.commandframework:$it:${property("cloud_version")}") }

	implementation("com.mineinabyss:protocolburrito:${property("protocolburrito_version")}") // Designed to be installed separately but uh.. :cringe:
	compileOnly("com.comphenix.protocol:ProtocolLib:${property("protocollib_version")}")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("kotlin_coroutines_version")}")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("kx_ser_version")}")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:${property("kx_ser_version")}")
	implementation("org.jetbrains.kotlin:kotlin-reflect:${property("kotlin_version")}")

	runtimeOnly("org.graalvm.polyglot:polyglot:${property("graal_sdk_version")}")
	runtimeOnly("org.graalvm.polyglot:python:${property("graal_sdk_version")}")
	implementation("org.graalvm.sdk:graal-sdk:${property("graal_sdk_version")}")
}

val version = property("mod_version")!!
val mc = property("minecraft_version")!!.toString()

tasks {
	build {
		dependsOn(reobfJar)
	}

	processResources {
		inputs.property("version", version)
		filesMatching("plugin.yml") {
			expand(mutableMapOf("version" to version))
		}
	}
}
