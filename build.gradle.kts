plugins {
	id("xyz.jpenilla.run-paper") version "1.0.6"
	id("org.jetbrains.kotlin.jvm") version "1.7.0"
	id("io.papermc.paperweight.userdev") version "1.3.7"
	id("com.github.johnrengelman.shadow") version "7.1.2"
	id("io.gitlab.arturbosch.detekt").version("1.20.0")
}

repositories {
	mavenCentral()

	maven("https://repo.aikar.co/content/groups/aikar/") // acf-paper
}

dependencies {
	paperDevBundle("1.19-R0.1-SNAPSHOT")
	implementation ("io.github.microutils:kotlin-logging-jvm:2.1.23")
	implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
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
		minecraftVersion("1.19")
	}

	detekt {
		toolVersion = "1.20.0"
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
