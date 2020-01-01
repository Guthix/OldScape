/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.server.api.Enums
import io.guthix.oldscape.server.world.entity.EntityAttribute
import io.guthix.oldscape.server.world.entity.character.player.Player
import java.io.IOException

var Player.gameframe by EntityAttribute<GameFrame>()

class GameFrame(
    val top: Int,
    val overlay: Int,
    val privateChatOverlay: Int,
    val expDisplay: Int,
    val modal: Int,
    val worldMap: Int,
    val topSection: Int,
    val dataOrbs: Int,
    val chatBox: Int,
    val dialogue: Int,
    val inventoryOptions: Int,
    val attackStylesTab: Int,
    val skillsTab: Int,
    val joerneyBookTab: Int,
    val itemBagTab: Int,
    val equipmentTab: Int,
    val prayerTab: Int,
    val spellBookTab: Int,
    val clanChatTab: Int,
    val friendsListTab: Int,
    val ignoreListTab: Int,
    val logoutTab: Int,
    val settingsTab: Int,
    val emotesTab: Int,
    val musicTab: Int
) {
    fun switchTo(player: Player, newDisplayType: GameFrame) {
        player.setTopInterface(newDisplayType.top)
        player.moveSubInterface(top, privateChatOverlay, newDisplayType.top, newDisplayType.privateChatOverlay)
        player.moveSubInterface(top, expDisplay, newDisplayType.top, newDisplayType.expDisplay)
        player.moveSubInterface(top, dataOrbs, newDisplayType.top, newDisplayType.dataOrbs)
        player.moveSubInterface(top, chatBox, newDisplayType.top, newDisplayType.chatBox)
        player.moveSubInterface(top, attackStylesTab, newDisplayType.top, newDisplayType.attackStylesTab)
        player.moveSubInterface(top, skillsTab, newDisplayType.top, newDisplayType.skillsTab)
        player.moveSubInterface(top, joerneyBookTab, newDisplayType.top, newDisplayType.joerneyBookTab)
        player.moveSubInterface(top, itemBagTab, newDisplayType.top, newDisplayType.itemBagTab)
        player.moveSubInterface(top, equipmentTab, newDisplayType.top, newDisplayType.equipmentTab)
        player.moveSubInterface(top, prayerTab, newDisplayType.top, newDisplayType.prayerTab)
        player.moveSubInterface(top, spellBookTab, newDisplayType.top, newDisplayType.spellBookTab)
        player.moveSubInterface(top, clanChatTab, newDisplayType.top, newDisplayType.clanChatTab)
        player.moveSubInterface(top, friendsListTab, newDisplayType.top, newDisplayType.friendsListTab)
        player.moveSubInterface(top, ignoreListTab, newDisplayType.top, newDisplayType.ignoreListTab)
        player.moveSubInterface(top, logoutTab, newDisplayType.top, newDisplayType.logoutTab)
        player.moveSubInterface(top, settingsTab, newDisplayType.top, newDisplayType.settingsTab)
        player.moveSubInterface(top, emotesTab, newDisplayType.top, newDisplayType.emotesTab)
        player.moveSubInterface(top, musicTab, newDisplayType.top, newDisplayType.musicTab)
    }

    companion object {
        val RESIZABLE_BOTTOM_LINE = GameFrame(
            top = 161,
            overlay = 4,
            privateChatOverlay = 7,
            expDisplay = 9,
            modal = 13,
            worldMap = 14,
            topSection = 27,
            dataOrbs = 28,
            chatBox = 29,
            dialogue = 549,
            inventoryOptions = 66,
            attackStylesTab = 68,
            skillsTab = 69,
            joerneyBookTab = 70,
            itemBagTab = 71,
            equipmentTab = 72,
            prayerTab = 73,
            spellBookTab = 74,
            clanChatTab = 75,
            friendsListTab = 76,
            ignoreListTab = 77,
            logoutTab = 78,
            settingsTab = 79,
            emotesTab = 80,
            musicTab = 81
        )

        val FIXED = loadFromEnum(1129)

        val BLACK_SCREEN = loadFromEnum(1132)

        val RESIZABLE_BOX = loadFromEnum(1131)

        fun loadFromEnum(enumId: Int): GameFrame {
            val enumConfig = Enums[enumId] ?: throw IOException("Enum id $enumId not found.")
            return GameFrame(
                top = (enumConfig.keyValuePairs.values.elementAt(0) as Int) shr 16,
                overlay = findEnumMapping(RESIZABLE_BOTTOM_LINE.overlay, enumConfig),
                privateChatOverlay = findEnumMapping(RESIZABLE_BOTTOM_LINE.privateChatOverlay, enumConfig),
                expDisplay = findEnumMapping(RESIZABLE_BOTTOM_LINE.expDisplay, enumConfig),
                modal = findEnumMapping(RESIZABLE_BOTTOM_LINE.modal, enumConfig),
                worldMap = findEnumMapping(RESIZABLE_BOTTOM_LINE.worldMap, enumConfig),
                topSection = findEnumMapping(RESIZABLE_BOTTOM_LINE.topSection, enumConfig),
                dataOrbs = findEnumMapping(RESIZABLE_BOTTOM_LINE.dataOrbs, enumConfig),
                chatBox = findEnumMapping(RESIZABLE_BOTTOM_LINE.chatBox, enumConfig),
                dialogue = findEnumMapping(RESIZABLE_BOTTOM_LINE.dialogue, enumConfig),
                inventoryOptions = findEnumMapping(RESIZABLE_BOTTOM_LINE.inventoryOptions, enumConfig),
                attackStylesTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.attackStylesTab, enumConfig),
                skillsTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.skillsTab, enumConfig),
                joerneyBookTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.joerneyBookTab, enumConfig),
                itemBagTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.itemBagTab, enumConfig),
                equipmentTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.equipmentTab, enumConfig),
                prayerTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.prayerTab, enumConfig),
                spellBookTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.spellBookTab, enumConfig),
                clanChatTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.clanChatTab, enumConfig),
                friendsListTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.friendsListTab, enumConfig),
                ignoreListTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.ignoreListTab, enumConfig),
                logoutTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.logoutTab, enumConfig),
                settingsTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.settingsTab, enumConfig),
                emotesTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.emotesTab, enumConfig),
                musicTab = findEnumMapping(RESIZABLE_BOTTOM_LINE.musicTab, enumConfig)
            )
        }

        private fun findEnumMapping(componentId: Int, enumConfig: EnumConfig): Int {
            for (pair in enumConfig.keyValuePairs) {
                if (pair.key and 0xFFFF == componentId) {
                    return (pair.value as Int) and 0xFFFF
                }
            }
            return componentId
        }
    }
}