plugins {
	id("io.papermc.paperweight.userdev")
	id("com.github.johnrengelman.shadow")
	kotlin("plugin.serialization")
}

repositories {
	maven("https://repo.mineinabyss.com/releases") // ProtocolBurrito
	maven("https://repo.aikar.co/content/groups/aikar/") // acf-paper
	maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
	mavenCentral()
}


dependencies {
	implementation(project(":common", "namedElements"))
	paperDevBundle("${property("minecraft_version")}-R0.1-SNAPSHOT")
	compileOnly("io.papermc.paper:paper-api:${property("minecraft_version")}-R0.1-SNAPSHOT")

	implementation("io.github.microutils:kotlin-logging-jvm:${property("kt_log_version")}")

	implementation("co.aikar:acf-paper:${property("acf_version")}")

	implementation("com.mineinabyss:protocolburrito:${property("protocolburrito_version")}") // Designed to be installed separately but uh.. :cringe:
	compileOnly("com.comphenix.protocol:ProtocolLib:${property("protocollib_version")}")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("kotlin_coroutines_version")}")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("kx_ser_version")}")

	implementation("space.vectrix.ignite:ignite-api:${property("ignite_version")}")
	implementation("org.spongepowered:mixin:${property("mixin_version")}")
}

val version = property("mod_version")!!
val mc = property("minecraft_version")!!.toString()

tasks {
	compileJava {
		options.compilerArgs.add("-parameters")
		options.isFork = true
	}

	build {
		dependsOn(reobfJar)
	}

	shadowJar {
		relocate("co.aikar.commands", "io.github.stellaricamc.stellarica.libraries.co.aikar.commands")
		relocate("co.aikar.locales", "io.github.stellaricamc.stellarica.libraries.co.aikar.locales")
	}

	reobfJar {
		remapperArgs.add("--mixin")
	}

	processResources {
		inputs.property("version", version)
		filesMatching("plugin.yml") {
			expand(mutableMapOf("version" to version))
		}
	}

	compileKotlin {
		kotlinOptions.javaParameters = true
		kotlinOptions.jvmTarget = "17"
	}
}