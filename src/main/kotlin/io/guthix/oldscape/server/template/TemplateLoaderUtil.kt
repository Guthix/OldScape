/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.template

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.FileNotFoundException
import java.net.URL
import java.nio.file.Path

private val yamlMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

fun <C> Any.readYaml(filePath: String): C = yamlMapper.readValue(
    Path.of(getResource(filePath).toURI()).toFile(),
    object : TypeReference<C>() {}
)

private fun Any.getResource(filePath: String): URL = javaClass.getResource(filePath) ?: throw FileNotFoundException(
    "Could not find resource file $filePath."
)