/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.cache.js5.Js5Cache
import io.guthix.cache.js5.container.Js5Container
import io.guthix.cache.js5.container.Js5Store
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.cache.js5.container.heap.Js5HeapStore
import io.guthix.oldscape.cache.BinariesArchive
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.MapArchive
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.api.Enums
import io.guthix.oldscape.server.api.EventBus
import io.guthix.oldscape.server.api.Huffman
import io.guthix.oldscape.server.api.Varbits
import io.guthix.oldscape.server.api.blueprint.LocationBlueprints
import io.guthix.oldscape.server.api.blueprint.ObjectBlueprints
import io.guthix.oldscape.server.net.OldScapeServer
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {
    OldScape.main(args)
}

object OldScape {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadConfig(Path.of(ServerConfig::class.java.getResource("/Config.yaml").toURI()))
        val cacheDir = Path.of(ServerConfig::class.java.getResource("/cache").toURI())
        val store = Js5HeapStore.open(Js5DiskStore.open(cacheDir), appendVersions = false)
        val cache = Js5Cache(store)
        store.write(Js5Store.MASTER_INDEX, Js5Store.MASTER_INDEX, Js5Container(
            cache.generateValidator(includeWhirlpool = false, includeSizes = false).encode()).encode()
        )
        val configArchive = cache.readArchive(ConfigArchive.id)
        Enums.load(configArchive)
        Varbits.load(configArchive)
        LocationBlueprints.load(configArchive)
        ObjectBlueprints.load(configArchive)
        val binariesArchive = cache.readArchive(BinariesArchive.id)
        Huffman.load(binariesArchive)
        EventBus.loadScripts()
        GamePacketDecoder.loadIncPackets()
        val mapSquareXteas = loadMapSquareXteaKeys(cacheDir.resolve("xteas.json"))
        val world = World()
        world.map.init(cache.readArchive(MapArchive.id), mapSquareXteas)
        Timer().scheduleAtFixedRate(world, 0, 600)
        OldScapeServer(config.revision, config.port, config.rsa.privateKey, config.rsa.modulus, world, store).run()
    }

    private fun loadConfig(path: Path) = ObjectMapper(YAMLFactory()).registerKotlinModule().readValue(
        path.toFile(), ServerConfig::class.java
    )

    private fun loadMapSquareXteaKeys(path: Path): List<MapXtea> {
        val mapper = ObjectMapper().registerKotlinModule()
        return mapper.readValue(path.toFile(), object : TypeReference<List<MapXtea>>(){})
    }
}