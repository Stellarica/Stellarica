plugins {
	java
	kotlin("jvm")
	id("fabric-loom") apply false
}

repositories {
	mavenCentral()
}

allprojects {
	apply {
		plugin("java")
		plugin("kotlin")
	}
	repositories {
		mavenCentral()
		mavenLocal()
		maven("https://jitpack.io")
	}
}
