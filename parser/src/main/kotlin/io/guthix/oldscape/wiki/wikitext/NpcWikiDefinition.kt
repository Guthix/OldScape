/*
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
 * along with Guthix OldScape-Wiki. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.wiki.wikitext

import io.guthix.oldscape.wiki.WikiConfigCompanion
import io.guthix.oldscape.wiki.WikiDefinition
import java.time.LocalDate

public class NpcWikiDefinition : WikiDefinition<NpcWikiDefinition>() {
    override var ids: List<Int>? = null
    override var name: String? = null
    public var type: String? = null
    public var release: LocalDate? = null
    public var update: String? = null
    public var isMembers: Boolean? = null
    public var examine: String? = null
    public var slayerLevel: Int? = null
    public var slayerXp: Double? = null
    public var isAggressive: Boolean? = null
    public var isPoisonous: Boolean? = null
    public var weakness: String? = null
    public var combatLvl: Int? = null
    public var hitPoints: Int? = null
    public var attackStyles: List<String>? = null
    public var maxHit: Int? = null
    public var attackSpeed: Int? = null
    public var isImmuneToPoison: Boolean? = null
    public var isImmuneToVenom: Boolean? = null
    public var category: String? = null
    public var assignedBySlayerMasters: MutableList<String>? = null
    public var attackStat: Int? = null
    public var strengthStat: Int? = null
    public var defenceStat: Int? = null
    public var magicStat: Int? = null
    public var rangeStat: Int? = null
    public var attackBonusMelee: Int? = null
    public var strBonus: Int? = null
    public var attBonusCrush: Int? = null
    public var attackBonusMagic: Int? = null
    public var attackBonusRange: Int? = null
    public var defBonusStab: Int? = null
    public var defBonusSlash: Int? = null
    public var defBonusCrush: Int? = null
    public var defBonusMagic: Int? = null
    public var defBonusRange: Int? = null
    public var strengthBonus: Int? = null
    public var rangeStrengthBonus: Int? = null
    public var attackBonus: Int? = null
    public var magicStrengthBonus: Int? = null

    override fun parseKeyValueLine(line: String, version: Int?) {
        when {
            line.checkWikiKey("id", version) -> ids = line.getIds()
            line.checkWikiKey("version", version) && type == null -> type = line.getWikiString()
            line.checkWikiKey("name", version) -> name = line.getWikiString()
            line.checkWikiKey("release", version) -> release = line.getWikiDate()
            line.checkWikiKey("update", version) -> update = line.getWikiString()
            line.checkWikiKey("members", version) -> isMembers = line.getWikiBool()
            line.checkWikiKey("combat", version) -> combatLvl = line.getWikiInt()
            line.checkWikiKey("examine", version) -> examine = line.getWikiString()
            line.checkWikiKey("hitpoints", version) -> hitPoints = line.getWikiInt()
            line.checkWikiKey("slaylvl", version) -> slayerLevel = line.getWikiNoInt()
            line.checkWikiKey("slayxp", version) -> slayerXp = line.getWikiNoDouble()
            line.checkWikiKey("aggressive", version) -> isAggressive = line.getWikiBool()
            line.checkWikiKey("weakness", version) -> weakness = line.getWikiString()
                ?.replace("[", "")?.replace("]", "")
            line.checkWikiKey("poisonous", version) -> isPoisonous = line.getWikiBool()
            line.checkWikiKey("immunepoison", version) -> isImmuneToPoison = line.getIsImmune()
            line.checkWikiKey("immunevenom", version) -> isImmuneToVenom = line.getIsImmune()
            line.checkWikiKey("attack speed", version) -> attackSpeed = line.getWikiInt()
            line.checkWikiKey("attack style", version) -> attackStyles = line.getWikiString()
                ?.replace("[", "")?.replace("]", "")
                ?.replace(" ", "")
                ?.split(",")
            line.checkWikiKey("max hit", version) -> maxHit = line.getWikiInt()
            line.checkWikiKey("cat", version) -> category = line.getWikiString()
            line.checkWikiKey("krystilia", version) -> line.addSlayerMaster("krystilia")
            line.checkWikiKey("vannaka", version) -> line.addSlayerMaster("vannaka")
            line.checkWikiKey("chaeldar", version) -> line.addSlayerMaster("chaeldar")
            line.checkWikiKey("nieve", version) -> line.addSlayerMaster("nieve")
            line.checkWikiKey("duradel", version) -> line.addSlayerMaster("duradel")
            line.checkWikiKey("att", version) -> attackStat = line.getWikiInt()
            line.checkWikiKey("str", version) -> strengthStat = line.getWikiInt()
            line.checkWikiKey("def", version) -> defenceStat = line.getWikiInt()
            line.checkWikiKey("mage", version) -> magicStat = line.getWikiInt()
            line.checkWikiKey("range", version) -> rangeStat = line.getWikiInt()
            line.checkWikiKey("attbns", version) -> attackBonusMelee = line.getWikiInt()
            line.checkWikiKey("arange", version) -> attackBonusRange = line.getWikiInt()
            line.checkWikiKey("amagic", version) -> attackBonusMagic = line.getWikiInt()
            line.checkWikiKey("strbns", version) -> strengthBonus = line.getWikiInt()
            line.checkWikiKey("rngbns", version) -> rangeStrengthBonus = line.getWikiInt()
            line.checkWikiKey("mbns", version) -> magicStrengthBonus = line.getWikiInt()
            line.checkWikiKey("dstab", version) -> defBonusStab = line.getWikiInt()
            line.checkWikiKey("dslash", version) -> defBonusSlash = line.getWikiInt()
            line.checkWikiKey("dcrush", version) -> defBonusCrush = line.getWikiInt()
            line.checkWikiKey("dmagic", version) -> defBonusMagic = line.getWikiInt()
            line.checkWikiKey("drange", version) -> defBonusRange = line.getWikiInt()
            else -> {
            }
        }
    }

    private fun String.getIsImmune(): Boolean? {
        val strValue = getWikiString()
        when {
            strValue.equals("No", ignoreCase = true) -> return false
            strValue.equals("Not immune", ignoreCase = true) -> return false
            strValue.equals("Yes", ignoreCase = true) -> return true
            strValue.equals("Immune", ignoreCase = true) -> return true
        }
        return null
    }

    private fun String.addSlayerMaster(slayerMaster: String) {
        val assignedByMaster = getWikiBool() ?: return
        if (assignedByMaster) {
            if (assignedBySlayerMasters == null) {
                assignedBySlayerMasters = mutableListOf()
            }
            assignedBySlayerMasters!!.add(slayerMaster)
        }
    }

    public companion object : WikiConfigCompanion() {
        override val queryString: String = "npc"
    }
}