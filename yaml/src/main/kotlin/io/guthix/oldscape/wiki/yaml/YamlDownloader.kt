/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
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
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.oldscape.server.blueprints.*
import io.guthix.oldscape.server.blueprints.equipment.EquipmentBlueprint
import io.guthix.oldscape.server.blueprints.equipment.ExtraEquipmentConfig
import io.guthix.oldscape.wiki.npcWikiDownloader
import io.guthix.oldscape.wiki.objectWikiDownloader
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
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
        val cacheDir = Path.of("../../server/src/main/resources/cache")
        println(cacheDir.toFile().absolutePath)
        val yamlFactory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        val objectMapper = ObjectMapper(yamlFactory)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerKotlinModule()

        val extraObjConfigs = objectWikiDownloader(cacheDir).filter { it.ids != null }
            .groupBy { it.isEquipable == true && it.slot != null }
        val normalObjectConfigs = extraObjConfigs[false]?.map { it.toExtraObjectConfig() }
            ?: throw IllegalStateException("No normal objects.")
        val equipmentConfigs = extraObjConfigs[true]
            ?: throw IllegalStateException("No equipable objects.")

        val objectFile = Path.of(javaClass.getResource("/").toURI()).resolve("Objects.yaml").toFile()
        objectMapper.writeValue(objectFile, normalObjectConfigs)
        logger.info { "Done writing objects to ${objectFile.absoluteFile.absolutePath}" }
        equipmentConfigs.groupBy { it.slot }.forEach { (slotStr, list) ->
            val fileName = slotStr?.replaceFirst(slotStr.first(), slotStr.first().toUpperCase())
            val eqFile = Path.of(javaClass.getResource("/").toURI())
                .resolve("${fileName}Equipment.yaml").toFile()
            objectMapper.writeValue(eqFile, list.map { it.toExtraEquipmentConfig() })
        }

        val npcFile = Path.of(javaClass.getResource("/").toURI()).resolve("Npcs.yaml").toFile()
        val npcWikiData = npcWikiDownloader(cacheDir).filter { it.ids != null }.map { it.toExtraEquipmentConfig() }
        objectMapper.writeValue(npcFile, npcWikiData)
        logger.info { "Done writing npcs to ${npcFile.absoluteFile.absolutePath}" }
    }
}

val equipmentSlotMapping = mapOf(
    "head" to EquipmentBlueprint.EquipmentSlot.HEAD,
    "cape" to EquipmentBlueprint.EquipmentSlot.CAPE,
    "neck" to EquipmentBlueprint.EquipmentSlot.NECK,
    "ammo" to EquipmentBlueprint.EquipmentSlot.AMMUNITION,
    "weapon" to EquipmentBlueprint.EquipmentSlot.WEAPON,
    "shield" to EquipmentBlueprint.EquipmentSlot.SHIELD,
    "2h" to EquipmentBlueprint.EquipmentSlot.TWO_HAND,
    "body" to EquipmentBlueprint.EquipmentSlot.BODY,
    "legs" to EquipmentBlueprint.EquipmentSlot.LEGS,
    "hands" to EquipmentBlueprint.EquipmentSlot.HANDS,
    "feet" to EquipmentBlueprint.EquipmentSlot.FEET,
    "ring" to EquipmentBlueprint.EquipmentSlot.RING
)

fun ObjectWikiDefinition.toExtraObjectConfig() = ExtraObjectConfig(
    ids!!,
    weight ?: 0f,
    examine ?: ""
)

fun ObjectWikiDefinition.toExtraEquipmentConfig(): ExtraObjectConfig {
    if(slot == null) throw IllegalStateException("Unmapped slot for id $ids found: $slot.")
    val equipment = EquipmentBlueprint.Equipment(
        StyleBonus(
            attBonusStab ?: 0,
            attBonusSlash ?: 0,
            attBonusCrush ?: 0,
            attBonusRange ?: 0,
            attBonusMagic ?: 0
        ),
        StyleBonus(
            defBonusStab ?: 0,
            defBonusSlash?: 0,
            defBonusCrush ?: 0,
            defBonusRange ?: 0,
            defBonusMagic ?: 0
        ),
        StrengthBonus(
            strengthBonus ?: 0,
            rangeStrengthBonus ?: 0,
            magicDamageBonus ?: 0
        ),
        prayerBonus ?: 0
    )

    return ExtraEquipmentConfig(
        ids!!,
        weight ?: 0f,
        examine ?: "",
        equipment
    )
}

fun NpcWikiDefinition.toExtraEquipmentConfig(): ExtraNpcConfig {
    val combat = if(combatLvl != null) {
        NpcBlueprint.Combat(
            isAggressive ?: false,
            isPoisonous ?: false,
            isImmuneToPoison ?: false,
            isImmuneToVenom ?: false,
            NpcBlueprint.Combat.Stats(
                hitPoints ?: 0,
                attackStat ?: 0,
                strengthStat ?: 0,
                defenceStat ?: 0,
                magicStat ?: 0,
                rangeStat ?: 0
            ),
            NpcBlueprint.Combat.AggressiveStats(
                attackBonusMelee ?: 0,
                attackBonusRange ?: 0,
                attackBonusMagic ?: 0,
                StrengthBonus(
                    strengthBonus ?: 0,
                    rangeStrengthBonus ?: 0,
                    magicStrengthBonus ?: 0
                )
            ),
            StyleBonus(
                defBonusStab ?: 0,
                defBonusSlash ?: 0,
                defBonusCrush ?: 0,
                defBonusRange ?: 0,
                defBonusMagic ?: 0
            )
        )
    } else null
    return ExtraNpcConfig(
        ids!!,
        examine ?: "",
        combat
    )
}