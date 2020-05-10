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
package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.LoginEvent
import io.guthix.oldscape.server.event.PlayerInitialized
import io.guthix.oldscape.server.event.script.EventBus

on(LoginEvent::class).then {
    player.initialize(world)
    for(skillId in 0 until 23) {
        player.updateStat(skillId, 13034431, 99)
    }
    player.updateWeight(100)
    player.updateRunEnergy(100)
    player.updateVarbit(8119, 1)
    player.senGameMessage("Welcome to OldScape Emulator!")
    player.updateContextMenu()
    EventBus.schedule(PlayerInitialized(), world, player)
}