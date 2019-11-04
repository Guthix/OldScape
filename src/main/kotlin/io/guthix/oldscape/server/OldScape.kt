/*
 * This file is part of Guthix OldScape.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Guthix OldScape. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server

import com.charleskorn.kaml.Yaml
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.server.net.OldScapeServer
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val configFile = Path.of(ServerConfig::class.java.getResource("/Config.yaml").toURI())
    val config = Yaml.default.parse(ServerConfig.serializer(), Files.readString(configFile))
    OldScapeServer(config.revision, config.port, 21, config.rsa.privateKey, config.rsa.modulus).run()
}