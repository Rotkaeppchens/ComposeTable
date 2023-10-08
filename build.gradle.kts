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

    implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
    }
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.animation)

    implementation(libs.kotlinx.datetime)

    // Hoplite for config reads
    implementation(libs.hoplite.core)
    implementation(libs.hoplite.yaml)
    implementation(libs.slf4j.nop)

    // Exposed for database access
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite)

    // Koin for Kotlin apps
    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    // Log4j for the LED library
    implementation(libs.log4j.core)

    implementation(libs.jserialcomm)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.AppImage, TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ComposeTable"
            packageVersion = "1.0.0"
        }

        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}
