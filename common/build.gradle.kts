plugins {
	kotlin("plugin.serialization")
	id("fabric-loom")
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings(loom.layered() {
		officialMojangMappings()
		"net.fabricmc:yarn:${property("yarn_mappings")}:v2"
	})

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("kx_ser_version")}")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:${property("kx_ser_version")}")
	compileOnly("io.github.microutils:kotlin-logging-jvm:${property("kt_log_version")}")
}
