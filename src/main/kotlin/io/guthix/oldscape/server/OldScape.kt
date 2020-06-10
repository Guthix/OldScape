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
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.api.*
import io.guthix.oldscape.server.blueprints.ExtraMonsterConfig
import io.guthix.oldscape.server.blueprints.ExtraNpcConfig
import io.guthix.oldscape.server.blueprints.ExtraObjectConfig
import io.guthix.oldscape.server.blueprints.equipment.ExtraBodyConfig
import io.guthix.oldscape.server.blueprints.equipment.ExtraEquipmentConfig
import io.guthix.oldscape.server.blueprints.equipment.ExtraHeadConfig
import io.guthix.oldscape.server.event.script.EventBus
import io.guthix.oldscape.server.net.OldScapeServer
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {
    OldScape.main(args)
}

object OldScape {
    @JvmStatic
    fun main(args: Array<String>) {
        val yamlMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        val config = yamlMapper.loadConfig(Path.of(javaClass.getResource("/Config.yaml").toURI()))

        val cacheDir = Path.of(javaClass.getResource("/cache").toURI())
        val store = Js5HeapStore.open(Js5DiskStore.open(cacheDir), appendVersions = false)
        val cache = Js5Cache(store)
        store.write(Js5Store.MASTER_INDEX, Js5Store.MASTER_INDEX, Js5Container(
            cache.generateValidator(includeWhirlpool = false, includeSizes = false).encode()).encode()
        )
        val configArchive = cache.readArchive(ConfigArchive.id)

        Enums.load(configArchive)
        InventoryBlueprints.load(configArchive)
        Varbits.load(configArchive)
        LocationBlueprints.load(configArchive)
        SequenceBlueprints.load(configArchive)
        SpotAnimBlueprints.load(configArchive)
        ObjectBlueprints.load(
            ObjectConfig.load(configArchive.readGroup(ObjectConfig.id)),
            yamlMapper.readObjectConfig("Objects.yaml"),
            yamlMapper.readHeadConfig("HeadEquipment.yaml"),
            yamlMapper.readEquipmentConfig("CapeEquipment.yaml"),
            yamlMapper.readEquipmentConfig("NeckEquipment.yaml"),
            yamlMapper.readEquipmentConfig("AmmunitionEquipment.yaml"),
            yamlMapper.readEquipmentConfig("WeaponEquipment.yaml"),
            yamlMapper.readEquipmentConfig("ShieldEquipment.yaml"),
            yamlMapper.readEquipmentConfig("TwoHandedEquipment.yaml"),
            yamlMapper.readBodyConfig("BodyEquipment.yaml"),
            yamlMapper.readEquipmentConfig("LegsEquipment.yaml"),
            yamlMapper.readEquipmentConfig("HandsEquipment.yaml"),
            yamlMapper.readEquipmentConfig("FeetEquipment.yaml"),
            yamlMapper.readEquipmentConfig("RingEquipment.yaml")
        )
        NpcBlueprints.load(
            NpcConfig.load(configArchive.readGroup(NpcConfig.id)),
            yamlMapper.readNpcConfig("Npcs.yaml"),
            yamlMapper.readMonsterConfig("Monsters.yaml")
        )
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

    private fun ObjectMapper.loadConfig(path: Path) = readValue(path.toFile(), ServerConfig::class.java)

    private fun loadMapSquareXteaKeys(path: Path): List<MapXtea> {
        val mapper = ObjectMapper().registerKotlinModule()
        return mapper.readValue(path.toFile(), object : TypeReference<List<MapXtea>>() {})
    }

    private fun ObjectMapper.readNpcConfig(filePath: String): List<ExtraNpcConfig> = readValue(
        Path.of(javaClass.getResource("/config/npcs/$filePath").toURI()).toFile(),
        object : TypeReference<List<ExtraNpcConfig>>() {}
    )

    private fun ObjectMapper.readMonsterConfig(filePath: String): List<ExtraMonsterConfig> = readValue(
        Path.of(javaClass.getResource("/config/npcs/$filePath").toURI()).toFile(),
        object : TypeReference<List<ExtraMonsterConfig>>() {}
    )

    private fun ObjectMapper.readObjectConfig(filePath: String): List<ExtraObjectConfig> = readValue(
        Path.of(javaClass.getResource("/config/objects/$filePath").toURI()).toFile(),
        object : TypeReference<List<ExtraObjectConfig>>() {}
    )

    private fun ObjectMapper.readEquipmentConfig(filePath: String): List<ExtraEquipmentConfig> = readValue(
        Path.of(javaClass.getResource("/config/objects/$filePath").toURI()).toFile(),
        object : TypeReference<List<ExtraEquipmentConfig>>() {}
    )

    private fun ObjectMapper.readHeadConfig(filePath: String): List<ExtraHeadConfig> = readValue(
        Path.of(javaClass.getResource("/config/objects/$filePath").toURI()).toFile(),
        object : TypeReference<List<ExtraHeadConfig>>() {}
    )

    private fun ObjectMapper.readBodyConfig(filePath: String): List<ExtraBodyConfig> = readValue(
        Path.of(javaClass.getResource("/config/objects/$filePath").toURI()).toFile(),
        object : TypeReference<List<ExtraBodyConfig>>() {}
    )
}