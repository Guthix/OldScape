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
package io.guthix.oldscape.wiki.wikitext

import io.guthix.oldscape.wiki.WikiTextParser
import java.time.LocalDate

class NpcWikiDefinition : WikiTextParser<NpcWikiDefinition>() {
    var versionType: String? = null
    var name: String? = null
    var release: LocalDate? = null
    var update: String? = null
    var isMembers: Boolean? = null
    var examine: String? = null
    var slayerLevel: Int? = null
    var slayerXp: Double? = null
    var isAggressive: Boolean? = null
    var isPoisonous: Boolean? = null
    var weakness: String? = null
    var combatLvl: Int? = null
    var hitPoints: Int? = null
    var attackStyle: String? = null
    var maxHit: Int? = null
    var attackSpeed: Int? = null
    var isImmuneToPoison: Boolean? = null
    var isImmuneToVenom: Boolean? = null
    var category: String? = null
    var assignedBySlayerMasters: MutableList<String>? = null
    var attackStat: Int? = null
    var strengthStat: Int? = null
    var defenceStat: Int? = null
    var mageStat: Int? = null
    var rangeState: Int? = null
    var attBonusStab: Int? = null
    var attBonusSlash: Int? = null
    var attBonusCrush: Int? = null
    var attBonusMagic: Int? = null
    var attBonusRange: Int? = null
    var defBonusStab: Int? = null
    var defBonusSlash: Int? = null
    var defBonusCrush: Int? = null
    var defBonusMagic: Int? = null
    var defBonusRange: Int? = null
    var strengthBonus: Int? = null
    var rangeStrengthBonus: Int? = null
    var attackBonus: Int? = null
    var magicBonus: Int? = null

    override fun parseKeyValueLine(line: String, version: Int?)  = when {
        line.checkWikiKey("id", version) -> ids = line.getIds()
        line.checkWikiKey("version", version) -> versionType = line.getWikiString()
        line.checkWikiKey("name", version) -> name = line.getWikiString()
        line.checkWikiKey("release", version) -> release = line.getWikiDate()
        line.checkWikiKey("update", version) -> update = line.getWikiString()
        line.checkWikiKey("members", version) -> isMembers = line.getWikiBool()
        line.checkWikiKey("combat", version) -> combatLvl = line.getWikiInt()
        line.checkWikiKey("examine", version) -> examine = line.getWikiString()
        line.checkWikiKey("hitpoints",version) -> hitPoints = line.getWikiInt()
        line.checkWikiKey("slaylvl", version) -> slayerLevel = line.getWikiNoInt()
        line.checkWikiKey("slayxp", version) -> slayerXp = line.getWikiNoDouble()
        line.checkWikiKey("aggressive", version) -> isAggressive = line.getWikiBool()
        line.checkWikiKey("weakness", version) -> weakness = line.getWikiString()
            ?.replace("[", "")?.replace("]", "")
        line.checkWikiKey("poisonous", version) -> isPoisonous = line.getWikiBool()
        line.checkWikiKey("immunepoison", version) -> isImmuneToPoison = line.getIsImmune()
        line.checkWikiKey("immunevenom", version) -> isImmuneToVenom = line.getIsImmune()
        line.checkWikiKey("attack speed", version) -> attackSpeed = line.getWikiInt()
        line.checkWikiKey("astyle", version) -> attackStyle = line.getWikiString()
        line.checkWikiKey("max hit", version) -> maxHit = line.getWikiInt()
        line.checkWikiKey("cat", version) -> category = line.getWikiString()
        line.checkWikiKey("krystilia", version) -> line.addSlayerMaster("krystilia")
        line.checkWikiKey("vannaka", version) -> line.addSlayerMaster("vannaka")
        line.checkWikiKey("chaeldar", version) -> line.addSlayerMaster("chaeldar")
        line.checkWikiKey("nieve", version) -> line.addSlayerMaster("nieve")
        line.checkWikiKey("duradel", version) -> line.addSlayerMaster("duradel")
        line.checkWikiKey("att", version) -> attackStat = line.getWikiInt()
        line.checkWikiKey("str",  version) -> strengthStat = line.getWikiInt()
        line.checkWikiKey("def", version) -> defenceStat = line.getWikiInt()
        line.checkWikiKey("mage", version) -> mageStat = line.getWikiInt()
        line.checkWikiKey("range", version) -> rangeState = line.getWikiInt()
        line.checkWikiKey("astab", version) -> attBonusStab = line.getWikiInt()
        line.checkWikiKey("aslash", version) -> attBonusSlash = line.getWikiInt()
        line.checkWikiKey("acrush", version) -> attBonusCrush = line.getWikiInt()
        line.checkWikiKey("amagic", version) -> attBonusMagic = line.getWikiInt()
        line.checkWikiKey("arange", version) -> attBonusRange = line.getWikiInt()
        line.checkWikiKey("dstab", version) -> defBonusStab = line.getWikiInt()
        line.checkWikiKey("dslash", version) -> defBonusSlash = line.getWikiInt()
        line.checkWikiKey("dcrush", version) -> defBonusCrush = line.getWikiInt()
        line.checkWikiKey("dmagic", version) -> defBonusMagic = line.getWikiInt()
        line.checkWikiKey("drange", version) -> defBonusRange = line.getWikiInt()
        line.checkWikiKey("strbns", version) -> strengthBonus = line.getWikiInt()
        line.checkWikiKey("rngbns", version) -> rangeStrengthBonus = line.getWikiInt()
        line.checkWikiKey("attbns", version) -> attackBonus = line.getWikiInt()
        line.checkWikiKey("mbns", version) -> magicBonus = line.getWikiInt()
        else -> {}
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
        if(assignedByMaster) {
            if(assignedBySlayerMasters == null) {
                assignedBySlayerMasters = mutableListOf()
            }
            assignedBySlayerMasters!!.add(slayerMaster)
        }
    }

    companion object {
        @JvmField val queryString = "npc"
    }
}