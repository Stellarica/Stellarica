plugins {
	java
	id("io.gitlab.arturbosch.detekt")
}

repositories {
	mavenCentral()
}
allprojects {
	repositories {
		mavenCentral()
		maven(uri("https://jitpack.io"))
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