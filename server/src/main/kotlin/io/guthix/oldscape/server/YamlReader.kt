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
package io.guthix.oldscape.server

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors

inline fun <reified C> Any.readYaml(filePath: String): C {
    val inStream = javaClass.getResourceAsStream(filePath).use {
        BufferedReader(InputStreamReader(it)).lines().parallel().collect(Collectors.joining("\n"))
    }
    return Yaml(
        configuration = YamlConfiguration(encodeDefaults = false, polymorphismStyle = PolymorphismStyle.Property)
    ).decodeFromString(inStream)
}


private fun Any.getResource(filePath: String): URL = javaClass.getResource(filePath) ?: throw FileNotFoundException(
    "Could not find resource file $filePath."
)