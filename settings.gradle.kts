// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("exposed-version", extra["exposed.version"] as String)
            version("hoplite-version", extra["hoplite.version"] as String)

            library("kotlinx.datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version(extra["kotlinx.datetime.version"] as String)

            library("koin.core", "io.insert-koin", "koin-core").version(extra["koin.core.version"] as String)
            library("koin.compose", "io.insert-koin", "koin-compose").version(extra["koin.compose.version"] as String)

            library("hoplite.core", "com.sksamuel.hoplite", "hoplite-core").versionRef("hoplite-version")
            library("hoplite.yaml", "com.sksamuel.hoplite", "hoplite-yaml").versionRef("hoplite-version")
            library("slf4j.nop", "org.slf4j", "slf4j-nop").version(extra["slf4j.version"] as String)

            library("exposed.core", "org.jetbrains.exposed", "exposed-core").versionRef("exposed-version")
            library("exposed.dao", "org.jetbrains.exposed", "exposed-dao").versionRef("exposed-version")
            library("exposed.jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed-version")
            library("sqlite", "org.xerial", "sqlite-jdbc").version(extra["sqlite.jdbc.version"] as String)

            // Log4J for the LED library
            library("log4j.core", "org.apache.logging.log4j", "log4j-core").version(extra["log4j.version"] as String)

            library("usb4java.javax", "org.usb4java", "usb4java-javax").version(extra["usb4java.version"] as String)
        }
    }
}

rootProject.name = "ComposeTable"

