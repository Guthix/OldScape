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
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.api.*
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.WorldInitializedEvent
import io.guthix.oldscape.server.net.OldScapeServer
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Loc
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Obj
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {
    OldScape.main(args)
}

object OldScape {
    @JvmStatic
    fun main(args: Array<String>) {
        val config: ServerConfig = readConfig("/Config.yaml")

        val cacheDir = Path.of(javaClass.getResource("/cache").toURI())
        val store = Js5HeapStore.open(Js5DiskStore.open(cacheDir), appendVersions = false)
        val cache = Js5Cache(store)
        store.write(Js5Store.MASTER_INDEX, Js5Store.MASTER_INDEX, Js5Container(
            cache.generateValidator(includeWhirlpool = false, includeSizes = false).encode()).encode()
        )
        val configArchive = cache.readArchive(ConfigArchive.id)

        EnumBlueprints.load(configArchive)
        InventoryBlueprints.load(configArchive)
        Varbits.load(configArchive)
        Loc.loadBlueprints(configArchive)
        SpotAnimBlueprints.load(configArchive)
        Obj.loadBlueprints(
            ObjectConfig.load(configArchive.readGroup(ObjectConfig.id)),
            readConfig("config/Objects.yaml")
        )
        Npc.loadBlueprints(
            NpcConfig.load(configArchive.readGroup(NpcConfig.id)),
            readConfig("config/Npcs.yaml")
        )
        val binariesArchive = cache.readArchive(BinariesArchive.id)
        Huffman.load(binariesArchive)


        EventBus.loadScripts()
        GamePacketDecoder.loadIncPackets()
        val mapSquareXteas = loadMapSquareXteaKeys(cacheDir.resolve("xteas.json"))
        val world = World()
        world.map.init(cache.readArchive(MapArchive.id), mapSquareXteas)
        EventBus.schedule(WorldInitializedEvent(world))
        Timer().scheduleAtFixedRate(world, 0, 600)
        OldScapeServer(config.revision, config.port, config.rsa.privateKey, config.rsa.modulus, world, store).run()
    }

    private fun ObjectMapper.loadConfig(path: Path) = readValue(path.toFile(), ServerConfig::class.java)

    private fun loadMapSquareXteaKeys(path: Path): List<MapXtea> {
        val mapper = ObjectMapper().registerKotlinModule()
        return mapper.readValue(path.toFile(), object : TypeReference<List<MapXtea>>() {})
    }
}