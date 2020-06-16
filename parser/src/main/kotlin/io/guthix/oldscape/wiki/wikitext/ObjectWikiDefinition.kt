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
package io.guthix.oldscape.wiki.wikitext

import io.guthix.oldscape.wiki.WikiConfigCompanion
import io.guthix.oldscape.wiki.WikiDefinition
import java.time.LocalDate

public class ObjectWikiDefinition : WikiDefinition<ObjectWikiDefinition>() {
    override var ids: List<Int>? = null
    override var name: String? = null
    public var versionType: String? = null
    public var releaseDate: LocalDate? = null
    public var update: String? = null
    public var isMembers: Boolean? = null
    public var quests: List<String>? = null
    public var isTradable: Boolean? = null
    public var isEquipable: Boolean? = null
    public var isStackable: Boolean? = null
    public var isNoteable: Boolean? = null
    public var hasPlaceHolder: Boolean? = null
    public var isAlchable: Boolean? = null
    public var destroy: String? = null
    public var valuePrice: Int? = null
    public var storePrice: List<Int>? = null
    public var seller: String? = null
    public var weight: Float? = null
    public var examine: String? = null
    public var onExchange: Boolean? = null

    //equipment
    public var slot: String? = null
    public var attBonusStab: Int? = null
    public var attBonusSlash: Int? = null
    public var attBonusCrush: Int? = null
    public var attBonusMagic: Int? = null
    public var attBonusRange: Int? = null
    public var defBonusStab: Int? = null
    public var defBonusSlash: Int? = null
    public var defBonusCrush: Int? = null
    public var defBonusMagic: Int? = null
    public var defBonusRange: Int? = null
    public var strengthBonus: Int? = null
    public var rangeStrengthBonus: Int? = null
    public var magicDamageBonus: Int? = null
    public var prayerBonus: Int? = null

    override fun parse(page: String, version: Int?): ObjectWikiDefinition {
        super.parse(page, version)
        return this
    }

    override fun parseKeyValueLine(line: String, version: Int?) {
        when {
            line.checkWikiKey("id", version) -> ids = line.getIds()
            line.checkWikiKey("versionType", version) -> versionType = line.getWikiString()
            line.checkWikiKey("name", version) -> name = line.getWikiString()
            line.checkWikiKey("release", version) -> releaseDate = line.getWikiDate()
            line.checkWikiKey("update", version) -> update = line.getWikiString()
            line.checkWikiKey("members", version) -> isMembers = line.getWikiBool()
            line.checkWikiKey("quest", version) -> {
                quests = if(line.getWikiString().equals("No", ignoreCase = true)) {
                    null
                } else {
                    line.getWikiStrings()
                        ?.map { it.replace("[", "").replace("]", "") }
                }
            }
            line.checkWikiKey("tradeable", version) -> isTradable = line.getWikiBool()
            line.checkWikiKey("equipable", version) -> isEquipable = line.getWikiBool()
            line.checkWikiKey("stackable", version) -> isStackable = line.getWikiBool()
            line.checkWikiKey("noteable", version) -> isNoteable = line.getWikiBool()
            line.checkWikiKey("placeholder", version) -> hasPlaceHolder = line.getWikiBool()
            line.checkWikiKey("alchable", version) -> isAlchable = line.getWikiBool()
            line.checkWikiKey("destroy", version) -> destroy = line.getWikiString()
            line.checkWikiKey("value", version) -> valuePrice = line.getWikiInt()
            line.checkWikiKey("store", version) ->  {
                storePrice = if(line.getWikiString().equals("No", ignoreCase = true)
                    || line.contains("Not sold", ignoreCase = true)
                ) {
                    null
                } else {
                    val prices = line.getWikiStrings()
                    prices?.map { it.replace(",", "").replace(" ", "").toInt() }
                }
            }

            line.checkWikiKey("seller", version) -> seller = line.getWikiString()
            line.checkWikiKey("weight", version) -> weight = line.getWikiFloat()
            line.checkWikiKey("examine", version) -> examine = line.getWikiString()
            line.checkWikiKey("exchange", version) -> onExchange =
                !line.getWikiString().equals("No", ignoreCase = true)
            line.checkWikiKey("slot", version) -> slot = line.getWikiString()
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
            line.checkWikiKey("str", version) -> strengthBonus = line.getWikiInt()
            line.checkWikiKey("rstr", version) -> rangeStrengthBonus = line.getWikiInt()
            line.checkWikiKey("mdmg", version) -> magicDamageBonus = line.getWikiInt()
            line.checkWikiKey("prayer", version) -> prayerBonus = line.getWikiInt()
            else -> { }
        }
    }

    public companion object : WikiConfigCompanion() {
        override val queryString: String = "item"
    }
}
