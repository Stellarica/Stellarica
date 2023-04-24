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
		maven(uri("https://jitpack.io"))
	}
	tasks.getByName("check") {
		this.setDependsOn(this.dependsOn.filterNot {
			it is TaskProvider<*> && it.name.contains("detekt")
		})
	}
}

tasks {
	build {
		doLast {
			copy {
				from("client/build/libs/client-${project.property("mod_version")}.jar")
				into("build/")
			}
			copy {
				from("server/build/libs/server-${project.property("mod_version")}.jar")
				into("build/")
			}
		}
	}
}

detekt {
	config = files("config/detekt/detekt.yml")
	buildUponDefaultConfig = true
}