import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.ldeutscher"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(fileTree("libs").matching {
        include("*.jar")
    })

    implementation(compose.desktop.currentOs)

    // Hoplite for config reads
    implementation(libs.hoplite.core)
    implementation(libs.hoplite.yaml)
    implementation(libs.slf4j.nop)

    // Koin for Kotlin apps
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.AppImage, TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ComposeTable"
            packageVersion = "1.0.0"
        }
    }
}
