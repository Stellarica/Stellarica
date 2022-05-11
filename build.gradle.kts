plugins {
	id("xyz.jpenilla.run-paper") version "1.0.6"
	id("org.jetbrains.kotlin.jvm") version "1.6.21"
	id("io.papermc.paperweight.userdev") version "1.3.6"
	id("com.github.johnrengelman.shadow") version "7.1.2"
	id("io.gitlab.arturbosch.detekt").version("1.20.0")
}

repositories {
	mavenCentral()

	maven("https://repo.aikar.co/content/groups/aikar/") // acf-paper
}

dependencies {
	paperDevBundle("1.18.2-R0.1-SNAPSHOT")

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
		minecraftVersion("1.18.2")
	}

	detekt {
		toolVersion = "1.20.0"
		config = files("config/detekt/detekt.yml")
		buildUponDefaultConfig = true
	}
	withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
		reports {
			html.required.set(true)
			txt.required.set(true)
		}
	}
}

java { toolchain.languageVersion.set(JavaLanguageVersion.of(17)) }