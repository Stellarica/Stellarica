plugins {
	id("io.papermc.paperweight.userdev") apply false
}

subprojects {
	apply(plugin = "io.papermc.paperweight.userdev")

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of("17"))
		}
	}

	tasks {
		compileKotlin {
			kotlinOptions.javaParameters = true
			kotlinOptions.jvmTarget = "17"
		}
	}
}