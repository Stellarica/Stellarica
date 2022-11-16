plugins {
	id("xyz.jpenilla.run-paper") version "2.0.0"
	id("org.jetbrains.kotlin.jvm") version "1.7.21"
	id("io.papermc.paperweight.userdev") version "1.3.9"
	id("com.github.johnrengelman.shadow") version "7.1.2"
	id("io.gitlab.arturbosch.detekt").version("1.22.0-RC2")
}

repositories {
	mavenCentral()
	maven("https://repo.mineinabyss.com/releases") // ProtocolBurrito
	maven("https://repo.aikar.co/content/groups/aikar/") // acf-paper
	maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
}

dependencies {
	paperDevBundle("1.19.2-R0.1-SNAPSHOT")
	implementation ("io.github.microutils:kotlin-logging-jvm:3.0.4")
	implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
	implementation("com.mineinabyss:protocolburrito:0.6.3") // Designed to be installed separately but uh.. :cringe:
	implementation("com.comphenix.protocol:ProtocolLib:4.8.0") // same here
}

tasks{
	compileJava {
		options.compilerArgs.add("-parameters")
		options.isFork = true
	}

	build {
		dependsOn(reobfJar)
	}

	reobfJar {
		outputJar.set(file(rootProject.projectDir.absolutePath + "/build/Hydrazine.jar"))
	}

	shadowJar {
		relocate("co.aikar.commands", "io.github.hydrazinemc.hydrazine.libraries.co.aikar.commands")
		relocate("co.aikar.locales", "io.github.hydrazinemc.hydrazine.libraries.co.aikar.locales")
	}

	compileKotlin {
		kotlinOptions.javaParameters = true
		kotlinOptions.jvmTarget = "17"
	}

	runServer {
		minecraftVersion("1.19.2")
	}

	detekt {
		config = files("config/detekt/detekt.yml")
		buildUponDefaultConfig = true
	}

	withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
		reports {
			// Only want .txt and .html
			// who the heck even uses .sarif?
			html.required.set(true)
			txt.required.set(true)
			xml.required.set(false)
			sarif.required.set(false)
		}
	}
}

java { toolchain.languageVersion.set(JavaLanguageVersion.of(17)) }
