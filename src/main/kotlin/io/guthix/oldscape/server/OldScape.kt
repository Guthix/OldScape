/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.oldscape.server.api.Enums
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.net.OldScapeServer
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import java.nio.file.Path
import java.util.*

object OldScape {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadConfig(Path.of(ServerConfig::class.java.getResource("/Config.yaml").toURI()))
        Cache.load(Path.of(ServerConfig::class.java.getResource("/cache").toURI()))
        Enums.load()
        EventBus.loadScripts()
        GamePacketDecoder.loadIncPackets()

        val world = World()
        Timer().scheduleAtFixedRate(world, 0, 600)
        OldScapeServer(config.revision, config.port, 21, world, config.rsa.privateKey, config.rsa.modulus).run()
    }

    private fun loadConfig(path: Path) = ObjectMapper(YAMLFactory()).registerKotlinModule().readValue(
        path.toFile(), ServerConfig::class.java
    )
}

