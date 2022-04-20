import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.21"
	id("io.papermc.paperweight.userdev") version "1.3.5"
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
	maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
	implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

	compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
	paperDevBundle("1.18.2-R0.1-SNAPSHOT")
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<KotlinCompile>().configureEach {
	kotlinOptions {
		jvmTarget = "17"
		javaParameters = true // https://github.com/aikar/commands/wiki/Gradle-Setup
	}
}

tasks{
	build {
		dependsOn(reobfJar)
	}
	reobfJar {
		outputJar.set(file(rootProject.projectDir.absolutePath + "/build/Hydrazine.jar"))
	}
}

