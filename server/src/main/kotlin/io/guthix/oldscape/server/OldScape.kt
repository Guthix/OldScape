/*
 * Copyright 2018-2021 Guthix
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

import ch.qos.logback.classic.util.ContextInitializer
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.Js5Container
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.js5.container.heap.Js5HeapStore
import io.guthix.oldscape.cache.BinariesArchive
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.MapArchive
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.db.PostgresDb
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.ServerBootEvent
import io.guthix.oldscape.server.event.WorldInitializedEvent
import io.guthix.oldscape.server.net.Huffman
import io.guthix.oldscape.server.net.OldScapeServer
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {
    OldScape.main(args)
}

object OldScape {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isNotEmpty() && args[0].equals("debug", true)) {
            System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "src/main/resources/logbackDebug.xml")
        }
        val config = readYaml<ServerConfig>("/Config.yaml")
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
        ServerContext.load(configArchive)
        Huffman.load(BinariesArchive.load(binaryArchive).huffman)

        EventBus.loadScripts()
        EventBus.execute(ServerBootEvent)
        GamePacketDecoder.loadIncPackets()
        PostgresDb.initialize(config.db)

        val mapSquareXteas = loadMapSquareXteaKeys(cacheDir.resolve("xteas.json"))
        val world = World.fromMap(
            MapArchive.load(
                cache.readArchive(MapArchive.id), mapSquareXteas.map {MapXtea(it.mapsquare, it.key)}
            ).mapsquares,
            mapSquareXteas.map { it.mapsquare to it.key }.toMap()
        )
        Timer().scheduleAtFixedRate(world, 0, 600)
        EventBus.execute(WorldInitializedEvent(world))
        OldScapeServer(config.revision, config.port, config.rsa.privateKey, config.rsa.modulus, world, store).run()
    }

    @Serializable // workaround for https://github.com/Kotlin/kotlinx.serialization/issues/532
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

    private fun loadMapSquareXteaKeys(path: Path): List<XteaConfig> = Json.decodeFromString(Files.readString(path))
}