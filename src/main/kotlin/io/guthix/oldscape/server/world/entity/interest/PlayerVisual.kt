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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.net.game.out.PlayerInfoPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.*
import io.guthix.oldscape.server.world.map.Tile
import io.netty.channel.ChannelFuture

class PlayerVisual(val username: String) : InterestManager, CharacterVisual() {
    override val updateFlags = sortedSetOf<PlayerInfoPacket.UpdateType>()

    var nameModifiers = arrayOf("", "", "")

    var inRunMode = false

    override var orientation: Int = 0

    var path = mutableListOf<Tile>()

    var sequence: Sequence? = null

    var spotAnimation: SpotAnimation? = null

    var publicMessage: PublicMessageEvent? = null

    var shoutMessage: String? = null

    var interacting: Character? = null

    val gender = PlayerManager.Gender.MALE

    val isSkulled = false

    val prayerIcon = -1

    var rights = 2

    val combatLevel = 126

    val style = PlayerManager.Style(
        hair = 0,
        beard = 10,
        torso = 18,
        arms = 26,
        legs = 36,
        hands = 33,
        feet = 42
    )

    val colours = PlayerManager.Colours(0, 0, 0, 0, 0)

    val equipment = PlayerManager.Equipment(null, null, null, null, null, null, null, null, null, null, null)

    val animations = PlayerManager.Animations(
        stand = 808,
        turn = 823,
        walk = 819,
        turn180 = 820,
        turn90CW = 821,
        turn90CCW = 822,
        run = 824
    )

    fun move() = if (path.isEmpty()) {
        movementType = MovementUpdateType.STAY
    } else {
        takeStep()
    }

    private fun takeStep() {
        lastPos = pos
        pos = when {
            inRunMode -> when {
                path.size == 1 -> {
                    movementType = MovementUpdateType.WALK
                    updateFlags.add(PlayerInfoPacket.movementTemporary)
                    followPosition = pos
                    path.removeAt(0)
                }
                path.size > 1 && pos.withInDistanceOf(path[1], 1.tiles) -> { // running corners
                    movementType = MovementUpdateType.WALK
                    followPosition = path.removeAt(0)
                    path.removeAt(0)
                }
                else -> {
                    movementType = MovementUpdateType.RUN
                    followPosition = path.removeAt(0)
                    path.removeAt(0)
                }
            }
            else -> {
                movementType = MovementUpdateType.WALK
                followPosition = pos
                path.removeAt(0)
            }
        }
        orientation = getOrientation(followPosition, pos)
    }

    override fun initialize(world: World, player: Player) {
        updateFlags.add(PlayerInfoPacket.appearance)
        updateFlags.add(PlayerInfoPacket.orientation)
        updateFlags.add(PlayerInfoPacket.nameModifiers)
    }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> = listOf()

    override fun postProcess() = updateFlags.clear()
}