plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
}

group = "com.aenadgrleey.kommander"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.core)
    implementation(libs.kotlin.serialization)
    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}