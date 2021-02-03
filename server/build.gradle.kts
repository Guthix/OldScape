@file:Suppress("ConvertLambdaToReference")

plugins {
    id("org.jetbrains.dokka")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "io.guthix.oldscape"
version = "0.1.0-SNAPSHOT"
description = "An Oldschool Runescape Server Emulator"

val kCoroutinesVersion: String by rootProject.extra
val classGraphVersion: String by extra("4.8.53")
val logbackVersion: String by extra("1.2.3")
val nettyVersion: String by extra("4.1.42.Final")
val ktSerVersion: String by extra("1.0.1")
val kamlVersion: String by extra("0.26.0")
val exposedVersion: String by extra("0.28.1")
val postgresVersion: String by extra("42.2.18")