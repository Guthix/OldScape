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
import io.guthix.oldscape.server.event.script.*
import io.guthix.oldscape.server.net.game.out.PlayerInfoPacket
import io.guthix.oldscape.server.world.entity.interest.InterestUpdateType
import io.guthix.oldscape.server.world.entity.interest.MovementInterestUpdate
import io.guthix.oldscape.server.world.map.Tile
import java.util.*
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.math.atan2

abstract class Character(val index: Int) : Entity() {
    internal val tasks = mutableMapOf<TaskType, MutableList<Task>>()

    internal abstract val updateFlags: SortedSet<out InterestUpdateType>

    var movementType: MovementInterestUpdate = MovementInterestUpdate.STAY

    abstract val size: TileUnit

    override val sizeX: TileUnit get() = size

    override val sizeY: TileUnit get() = size

    override var pos: Tile = Tile(0.floors, 3231.tiles, 3222.tiles)

    var lastPos: Tile = Tile(0.floors, 3231.tiles, 3222.tiles)

    var followPosition: Tile = lastPos.copy()

    var interacting: Character? = null

    var sequence: Sequence? = null

    var spotAnimation: SpotAnimation? = null

    var shoutMessage: String? = null

    var path: MutableList<Tile> = mutableListOf()

    var inRunMode: Boolean = false

    fun move() {
        lastPos = pos
        if (path.isEmpty()) {
            movementType = MovementInterestUpdate.STAY
        } else {
            takeStep()
        }
    }

    private fun takeStep() {
        pos = when {
            inRunMode -> when {
                path.size == 1 -> {
                    movementType = MovementInterestUpdate.WALK
                    if (this is Player) updateFlags.add(PlayerInfoPacket.movementTemporary) // TODO improve this
                    followPosition = pos
                    path.removeAt(0)
                }
                path.size > 1 && pos.withInDistanceOf(path[1], 1.tiles) -> { // running corners
                    movementType = MovementInterestUpdate.WALK
                    followPosition = path.removeAt(0)
                    path.removeAt(0)
                }
                else -> {
                    movementType = MovementInterestUpdate.RUN
                    followPosition = path.removeAt(0)
                    path.removeAt(0)
                }
            }
            else -> {
                movementType = MovementInterestUpdate.WALK
                followPosition = pos
                path.removeAt(0)
            }
        }
        orientation = getOrientation(followPosition, pos)
    }

    fun turnTo(entity: Entity) {
        setOrientation(entity)
        addOrientationFlag()
    }

    fun turnToLock(char: Character?) {
        interacting = char
        char?.let { setOrientation(char) }
        addTurnToLockFlag()
    }

    fun animate(animation: Sequence) {
        sequence = animation
        addSequenceFlag()
        addTask(NormalTask) {
            val duration = sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because sequence does not exist."
            )
            wait(ticks = duration)
            sequence = null
        }
    }

    fun stopAnimation() {
        sequence = null
        addSequenceFlag()
        cancelTask(NormalTask)
    }

    fun spotAnimate(spotAnim: SpotAnimation) {
        spotAnimation = spotAnim
        addSpotAnimationFlag()
        addTask(NormalTask) {
            val duration = spotAnimation?.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because spot animation or sequence does not exist."
            )
            wait(ticks = duration)
            spotAnimation = null
        }
    }

    fun stopSpotAnimation() {
        spotAnimation = null
        addSpotAnimationFlag()
        cancelTask(NormalTask)
    }

    var health: Int = 100

    val hitMarkQueue: MutableList<HitMark> = mutableListOf()

    val healthBarQueue: MutableList<HealthBar> = mutableListOf()

    fun hit(colour: HitMark.Colour, damage: Int, delay: Int) {
        addHitUpdateFlag()
        hitMarkQueue.add(HitMark(colour, damage, delay))
        healthBarQueue.add(HealthBar(2, 0, 0, 100)) // TODO do something better here
    }

    open fun postProcess() {
        updateFlags.clear()
        hitMarkQueue.clear()
        healthBarQueue.clear()
        tasks.values.forEach { it.forEach(Task::postProcess) }
    }

    protected abstract fun addOrientationFlag(): Boolean

    protected abstract fun addTurnToLockFlag(): Boolean

    protected abstract fun addSequenceFlag(): Boolean

    protected abstract fun addSpotAnimationFlag(): Boolean

    protected abstract fun addHitUpdateFlag(): Boolean

    abstract fun processTasks()

    fun addTask(type: TaskType, replace: Boolean = false, r: suspend Task.() -> Unit): Task {
        val task = Task(type, this)
        task.next = ConditionalContinuation(TrueCondition, r.createCoroutineUnintercepted(task, task))
        if (replace) {
            val toRemove = tasks.remove(type)
            toRemove?.forEach(Task::cancel)
            tasks[type] = mutableListOf(task)
        } else {
            tasks.getOrPut(type) { mutableListOf() }.add(task)
        }
        return task
    }

    fun cancelTask(type: TaskType) {
        tasks[type]?.forEach(Task::cancel)
    }

    fun getOrientation(prev: Tile, new: Tile): Int = getOrientation(new.x - prev.x, new.y - prev.y)

    fun getOrientation(dx: TileUnit, dy: TileUnit): Int = moveDirection[2 - dy.value][dx.value + 2]

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