plugins {
	id("com.github.johnrengelman.shadow")
}

repositories {
	mavenCentral()
}

dependencies {
	paperDevBundle("${property("minecraft_version")}-R0.1-SNAPSHOT")
	compileOnly("io.papermc.paper:paper-api:${property("minecraft_version")}-R0.1-SNAPSHOT")
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

	reobfJar {
		remapperArgs.add("--mixin")
	}
}