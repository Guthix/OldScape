package io.guthix.oldscape

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

fun DependencyHandler.contentPlugin(name: String): Dependency? =
    add("implementation", project(":server:world:plugins:content:$name"))

fun DependencyHandler.corePlugin(name: String): Dependency? =
    add("implementation", project(":server:world:plugins:core:$name"))

