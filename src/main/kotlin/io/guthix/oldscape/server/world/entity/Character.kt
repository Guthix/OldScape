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
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.floors
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.world.entity.interest.InterestUpdateType
import io.guthix.oldscape.server.world.entity.interest.MovementInterestUpdate
import io.guthix.oldscape.server.world.map.Tile
import java.util.*
import kotlin.math.atan2

abstract class Character(val index: Int) : Entity() {
    internal abstract val updateFlags: SortedSet<out InterestUpdateType>

    var movementType = MovementInterestUpdate.STAY

    abstract val size: TileUnit

    override val sizeX get() = size

    override val sizeY get() = size

    override var pos = Tile(0.floors, 3231.tiles, 3222.tiles)

    var lastPos = Tile(0.floors, 3231.tiles, 3222.tiles)

    var followPosition = lastPos.copy()

    var interacting: Character? = null

    fun turnTo(entity: Entity) {
        setOrientation(entity)
        addOrientationFlag()
    }

    fun turnToLock(char: Character?) {
        interacting = char
        char?.let { setOrientation(char) }
        addTurnToLockFlag()
    }

    protected abstract fun addOrientationFlag(): Boolean

    protected abstract fun addTurnToLockFlag(): Boolean

    fun getOrientation(prev: Tile, new: Tile) = getOrientation(new.x - prev.x, new.y - prev.y)

    fun getOrientation(dx: TileUnit, dy: TileUnit) = moveDirection[2 - dy.value][dx.value + 2]

    protected fun setOrientation(entity: Entity) {
        val dx = (pos.x.value + (sizeX.value.toDouble() / 2)) -
            (entity.pos.x.value + (entity.sizeX.value.toDouble() / 2))
        val dy = (pos.y.value + (sizeY.value.toDouble() / 2)) -
            (entity.pos.y.value + (entity.sizeY.value.toDouble() / 2))
        if (dx.toInt() != 0 || dy.toInt() != 0) orientation = (atan2(dx, dy) * 325.949).toInt() and 0x7FF
    }

    companion object {
        private val moveDirection = arrayOf(
            intArrayOf(768, 768, 1024, 1280, 1280),
            intArrayOf(768, 768, 1024, 1280, 1280),
            intArrayOf(512, 512, -1, 1536, 1536),
            intArrayOf(256, 256, 0, 1792, 1792),
            intArrayOf(256, 256, 0, 1792, 1792)
        )
    }
}