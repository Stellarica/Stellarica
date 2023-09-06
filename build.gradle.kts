plugins {
	java
	kotlin("jvm")
	id("io.gitlab.arturbosch.detekt")
	id("fabric-loom") apply false
}

repositories {
	mavenCentral()
}

allprojects {
	apply {
		plugin("java")
		plugin("kotlin")
		plugin("io.gitlab.arturbosch.detekt")
	}
	repositories {
		mavenCentral()
		mavenLocal()
		maven(uri("https://jitpack.io"))
	}
	tasks.getByName("check") {
		this.setDependsOn(this.dependsOn.filterNot {
			it is TaskProvider<*> && it.name.contains("detekt")
		})
	}
}

detekt {
	config = files("config/detekt/detekt.yml")
	buildUponDefaultConfig = true
}