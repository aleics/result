plugins {
    kotlin("jvm") version "1.7.21"
    id("io.gitlab.arturbosch.detekt").version("1.22.0")
}

group = "me.aleics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
