import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.aenadgrleey.kommander"

dependencies {
    implementation(libs.bundles.core)
    implementation(libs.bundles.ui)
    implementation(project(":core"))
    implementation(project(":features:root"))
    implementation(project(":features:runner"))
    implementation(project(":features:editor"))
    implementation(project(":features:menu"))
    implementation(project(":features:actions"))
    implementation(project(":features:lsp"))
}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "Kommander"
            packageVersion = "1.0.0"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}