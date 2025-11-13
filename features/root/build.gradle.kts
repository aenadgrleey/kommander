plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.aenadgrleey.kommander"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    implementation(project(":features:lsp"))
    implementation(libs.bundles.ui)
    implementation(compose.desktop.currentOs)
    implementation(libs.bundles.core)
    testImplementation(libs.bundles.test)

}

tasks.test {
    useJUnitPlatform()
}