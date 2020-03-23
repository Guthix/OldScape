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


        val objectFile = Path.of(javaClass.getResource("/").toURI()).resolve("Objects.yaml").toFile()
        val extraObjConfigs = objectWikiDownloader(cacheDir).filter { it.ids != null }.map { it.toExtraConfig() }
        objectMapper.writeValue(objectFile, extraObjConfigs)
        logger.info { "Done writing objects to ${objectFile.absoluteFile.absolutePath}" }

        val npcFile = Path.of(javaClass.getResource("/").toURI()).resolve("Npcs.yaml").toFile()
        val npcWikiData = npcWikiDownloader(cacheDir).filter { it.ids != null }.map { it.toExtraConfig() }
        objectMapper.writeValue(npcFile, npcWikiData)
        logger.info { "Done writing npcs to ${npcFile.absoluteFile.absolutePath}" }
    }
}

val equipmentSlotMapping = mapOf(
    "head" to EquipmentSlot.HEAD,
    "cape" to EquipmentSlot.CAPE,
    "neck" to EquipmentSlot.NECK,
    "ammo" to EquipmentSlot.AMMUNITION,
    "weapon" to EquipmentSlot.WEAPON,
    "shield" to EquipmentSlot.SHIELD,
    "2h" to EquipmentSlot.TWO_HAND,
    "body" to EquipmentSlot.BODY,
    "legs" to EquipmentSlot.LEGS,
    "hands" to EquipmentSlot.HANDS,
    "feet" to EquipmentSlot.FEET,
    "ring" to EquipmentSlot.RING
)

fun ObjectWikiDefinition.toExtraConfig(): ExtraObjectConfig {
    val equipment = if(isEquipable == true && slot != null) {
        val slot = equipmentSlotMapping[slot!!] ?: throw IllegalStateException("Unmapped slot found: $slot.")
        ObjectBlueprint.Equipment(
            slot,
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
    } else null
    return ExtraObjectConfig(
        ids!!,
        weight ?: 0f,
        examine ?: "",
        equipment
    )
}

fun NpcWikiDefinition.toExtraConfig(): ExtraNpcConfig {
    val combat = if(combatLvl != null) {
        NpcBlueprint.Combat(
            combatLvl!!,
            attackStyles ?: emptyList(),
            attackSpeed ?: 1,
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