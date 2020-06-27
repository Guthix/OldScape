/**
 * This file is part of Guthix OldScape-Wiki.
 *
 * Guthix OldScape-Wiki is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Wiki is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.wiki.yaml

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.oldscape.server.blueprints.*
import io.guthix.oldscape.server.blueprints.equipment.ExtraBodyConfig
import io.guthix.oldscape.server.blueprints.equipment.ExtraHeadConfig
import io.guthix.oldscape.wiki.npcWikiDownloader
import io.guthix.oldscape.wiki.objectWikiDownloader
import io.guthix.oldscape.wiki.wikitext.ObjectWikiDefinition
import mu.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger {  }

fun main(args: Array<String>) {
    YamlDownloader.main(args)
}

object YamlDownloader {
    @JvmStatic
    fun main(args: Array<String>) {
        val serverDir = Path.of("../../Oldscape-Server/src/main/resources")
        val cacheDir = serverDir.resolve("cache")
        val configDir = serverDir.resolve("config")
        val npcDir = configDir.resolve("npcs")
        val objDir = configDir.resolve("objects")

        val yamlFactory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        val objectMapper = ObjectMapper(yamlFactory)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerKotlinModule()

        objectMapper.writeObjs(cacheDir, objDir)
        objectMapper.writeNpcs(cacheDir, npcDir, "Npcs.yaml")
    }

    fun ObjectMapper.writeObjs(cacheDir: Path, objServerDir: Path) {
        val objWikiData = objectWikiDownloader(cacheDir).filter { it.ids != null }.sortedBy { it.ids!!.first() }
        val equpimentWikiData = objWikiData.filter { it.slot != null }

        val objFileName = "Objects.yaml"
        val objData = objWikiData.filter { it.slot == null }
        val objectFile = Path.of(javaClass.getResource("/").toURI()).resolve(objFileName).toFile()
        writeValue(objectFile, objData.map(ObjectWikiDefinition::toExtraObjectConfig))
        logger.info {
            "Done writing ${objData.size} obj configs to ${objectFile.absoluteFile.absolutePath}"
        }


        val ammoFileName = "AmmunitionEquipment.yaml"
        val ammoData = equpimentWikiData.filter { it.slot!!.equals("ammo", true) }
        val ammunitionFile = Path.of(javaClass.getResource("/").toURI()).resolve(ammoFileName).toFile()
        writeValue(ammunitionFile, ammoData.map(ObjectWikiDefinition::toExtraEquipmentConfig))
        logger.info {
            "Done writing ${ammoData.size} ammunition equipment configs to ${ammunitionFile.absoluteFile.absolutePath}"
        }

        val bodyFileName = "BodyEquipment.yaml"
        val bodyData = equpimentWikiData.filter { it.slot!!.equals("body", true) }
        val bodyFile = Path.of(javaClass.getResource("/").toURI()).resolve(bodyFileName).toFile()
        val bodyServerConfigs = readValue<List<ExtraBodyConfig>>(objServerDir.resolve(bodyFileName).toFile())
        writeValue(bodyFile, bodyData.map { new ->
            val curExtraConfig = bodyServerConfigs.find { (ids) -> new.ids == ids }
            new.toExtraBodyConfig(curExtraConfig)
        })
        logger.info {
            "Done writing ${bodyData.size} body equipment configs to ${bodyFile.absoluteFile.absolutePath}"
        }

        val capeFileName = "CapeEquipment.yaml"
        val capeData = equpimentWikiData.filter { it.slot!!.equals("cape", true) }
        val capeFile = Path.of(javaClass.getResource("/").toURI()).resolve(capeFileName).toFile()
        writeValue(capeFile, capeData.map(ObjectWikiDefinition::toExtraEquipmentConfig))
        logger.info {
            "Done writing ${capeData.size} cape equipment configs to ${capeFile.absoluteFile.absolutePath}"
        }

        val feetFileName = "FeetEquipment.yaml"
        val feetData = equpimentWikiData.filter { it.slot!!.equals("feet", true) }
        val feetFile = Path.of(javaClass.getResource("/").toURI()).resolve(feetFileName).toFile()
        writeValue(feetFile, feetData.map(ObjectWikiDefinition::toExtraEquipmentConfig))
        logger.info {
            "Done writing ${feetData.size} feet equipment configs to ${feetFile.absoluteFile.absolutePath}"
        }

        val handFileName = "HandEquipment.yaml"
        val handData = equpimentWikiData.filter { it.slot!!.equals("hands", true) }
        val handFile = Path.of(javaClass.getResource("/").toURI()).resolve(handFileName).toFile()
        writeValue(handFile, handData.map(ObjectWikiDefinition::toExtraEquipmentConfig))
        logger.info {
            "Done writing ${handData.size} hand equipment configs to ${handFile.absoluteFile.absolutePath}"
        }


        val headFileName = "HeadEquipment.yaml"
        val headData = equpimentWikiData.filter { it.slot!!.equals("head", true) }
        val headFile = Path.of(javaClass.getResource("/").toURI()).resolve(headFileName).toFile()
        val headServerConfigs = readValue<List<ExtraHeadConfig>>(objServerDir.resolve(headFileName).toFile())
        writeValue(headFile, headData.map { new ->
            val curExtraConfig = headServerConfigs.find { (ids) -> new.ids == ids }
            new.toExtraHeadConfig(curExtraConfig)
        })
        logger.info {
            "Done writing ${headData.size} head equipment configs to ${headFile.absoluteFile.absolutePath}"
        }

        val legFileName = "LegEquipment.yaml"
        val legData = equpimentWikiData.filter { it.slot!!.equals("legs", true) }
        val legFile = Path.of(javaClass.getResource("/").toURI()).resolve(legFileName).toFile()
        writeValue(legFile, legData.map(ObjectWikiDefinition::toExtraEquipmentConfig))
        logger.info {
            "Done writing ${legData.size} leg equipment configs to ${legFile.absoluteFile.absolutePath}"
        }


        val neckFileName = "NeckEquipment.yaml"
        val neckData = equpimentWikiData.filter { it.slot!!.equals("neck", true) }
        val neckFile = Path.of(javaClass.getResource("/").toURI()).resolve(neckFileName).toFile()
        writeValue(neckFile, neckData.map(ObjectWikiDefinition::toExtraEquipmentConfig))
        logger.info {
            "Done writing ${neckData.size} neck equipment configs to ${neckFile.absoluteFile.absolutePath}"
        }

        val ringFileName = "RingEquipment.yaml"
        val ringData = equpimentWikiData.filter { it.slot!!.equals("neck", true) }
        val ringFile = Path.of(javaClass.getResource("/").toURI()).resolve(ringFileName).toFile()
        writeValue(ringFile, ringData.map(ObjectWikiDefinition::toExtraEquipmentConfig))
        logger.info {
            "Done writing ${ringData.size} ring equipment configs to ${ringFile.absoluteFile.absolutePath}"
        }

        val shieldFileName = "ShieldEquipment.yaml"
        val shieldData = equpimentWikiData.filter { it.slot!!.equals("shield", true) }
        val shieldFile = Path.of(javaClass.getResource("/").toURI()).resolve(shieldFileName).toFile()
        writeValue(shieldFile, shieldData.map(ObjectWikiDefinition::toExtraEquipmentConfig))
        logger.info {
            "Done writing ${shieldData.size} shield equipment configs to ${shieldFile.absoluteFile.absolutePath}"
        }

        val twoHandFileName = "TwoHandEquipment.yaml"
        val twoHandData = equpimentWikiData.filter { it.slot!!.equals("2h", true) }
        val twoHandFile = Path.of(javaClass.getResource("/").toURI()).resolve(twoHandFileName).toFile()
        writeValue(twoHandFile, twoHandData.map(ObjectWikiDefinition::toExtraWeaponConfig))
        logger.info {
            "Done writing ${twoHandData.size} two hand equipment configs to ${twoHandFile.absoluteFile.absolutePath}"
        }

        val weaponFileName = "WeaponEquipment.yaml"
        val weaponData = equpimentWikiData.filter { it.slot!!.equals("weapon", true) }
        val weaponFile = Path.of(javaClass.getResource("/").toURI()).resolve(weaponFileName).toFile()
        writeValue(weaponFile, weaponData.map(ObjectWikiDefinition::toExtraWeaponConfig))
        logger.info {
            "Done writing ${weaponData.size} weapon equipment configs to ${weaponFile.absoluteFile.absolutePath}"
        }
    }

    fun ObjectMapper.writeNpcs(cacheDir: Path, npcServerDir: Path, fileName: String) {
        val npcData = npcWikiDownloader(cacheDir).filter { it.ids != null }.sortedBy { it.ids!!.first() }
        val npcFile = Path.of(javaClass.getResource("/").toURI()).resolve(fileName).toFile()
        val npcServerConfigs = readValue<List<ExtraNpcConfig>>(npcServerDir.resolve(fileName).toFile())
        writeValue(npcFile, npcData.map { new ->
            val curExtraConfig = npcServerConfigs.find { cur -> new.ids == cur.ids }
            new.toExtraNpcConfig(curExtraConfig)
        })
        logger.info {
            "Done writing ${npcData.size} npcs to ${npcFile.absoluteFile.absolutePath}"
        }
    }
}

