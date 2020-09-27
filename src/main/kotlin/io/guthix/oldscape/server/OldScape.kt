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
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.Js5Container
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.js5.container.heap.Js5HeapStore
import io.guthix.oldscape.cache.BinariesArchive
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.MapArchive
import io.guthix.oldscape.cache.config.*
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.InitializeTemplateEvent
import io.guthix.oldscape.server.event.WorldInitializedEvent
import io.guthix.oldscape.server.net.Huffman
import io.guthix.oldscape.server.net.OldScapeServer
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.World
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {
    OldScape.main(args)
}

object OldScape {
    @JvmStatic
    fun main(args: Array<String>) {
        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        val config = mapper.readValue(javaClass.getResource("/Config.yaml"), ServerConfig::class.java)

        val cacheDir = Path.of(javaClass.getResource("/cache").toURI())
        val store = Js5DiskStore.open(cacheDir).use {
            Js5HeapStore.open(it, appendVersions = false)
        }
        val cache = Js5Cache(store)
        store.write(Js5Store.MASTER_INDEX, Js5Store.MASTER_INDEX, Js5Container(
            cache.generateValidator(includeWhirlpool = false, includeSizes = false).encode()).encode()
        )

        val configArchive = cache.readArchive(ConfigArchive.id)
        val binaryArchive = cache.readArchive(BinariesArchive.id)

        VarbitTemplates.load(VarbitConfig.load(configArchive.readGroup(VarbitConfig.id)), ::VarbitTemplate)
        InventoryTemplates.load(InventoryConfig.load(configArchive.readGroup(InventoryConfig.id)), ::InventoryTemplate)
        SequenceTemplates.load(SequenceConfig.load(configArchive.readGroup(SequenceConfig.id)), ::SequenceTemplate)
        SpotAnimTemplates.load(SpotAnimConfig.load(configArchive.readGroup(SpotAnimConfig.id)), ::SpotAnimTemplate)
        EnumTemplates.load(EnumConfig.load(configArchive.readGroup(EnumConfig.id)), ::EnumTemplate)
        LocTemplates.load(LocationConfig.load(configArchive.readGroup(LocationConfig.id)), ::LocTemplate)
        NpcTemplates.load(NpcConfig.load(configArchive.readGroup(NpcConfig.id)), ::NpcTemplate)
        ObjTemplates.load(ObjectConfig.load(configArchive.readGroup(ObjectConfig.id)), ::ObjTemplate)
        Huffman.load(BinariesArchive.load(binaryArchive).huffman)

        EventBus.loadScripts()
        EventBus.execute(InitializeTemplateEvent)
        GamePacketDecoder.loadIncPackets()

        val mapSquareXteas = loadMapSquareXteaKeys(cacheDir.resolve("xteas.json"))
        val world = World()
        world.map.init(cache.readArchive(MapArchive.id), mapSquareXteas)
        EventBus.schedule(WorldInitializedEvent(world))
        Timer().scheduleAtFixedRate(world, 0, 600)
        OldScapeServer(config.revision, config.port, config.rsa.privateKey, config.rsa.modulus, world, store).run()
    }

    data class XteaConfig(val mapsquare: Int, val key: IntArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as XteaConfig
            if (mapsquare != other.mapsquare) return false
            if (!key.contentEquals(other.key)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = mapsquare
            result = 31 * result + key.contentHashCode()
            return result
        }
    }

    private fun loadMapSquareXteaKeys(path: Path): List<MapXtea> = ObjectMapper().registerKotlinModule()
        .readValue(path.toFile(), object : TypeReference<List<XteaConfig>>() {}).map { MapXtea(it.mapsquare, it.key) }

    private fun ObjectMapper.loadConfig(path: Path) = readValue(path.toFile(), ServerConfig::class.java)
}