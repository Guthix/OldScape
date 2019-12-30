package io.guthix.oldscape.server.world.entity.character

import io.guthix.oldscape.server.world.entity.Entity
import io.guthix.oldscape.server.world.mapsquare.floor
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tile
import kotlin.reflect.KProperty

abstract class Character(attributes: MutableMap<KProperty<*>, Any?>) : Entity(attributes) {
    val position = Tile(0.floor, 3222.tile, 3218.tile)

    val lastPostion = position

    val interestMovementUpdateType = MovementUpdateType.STAY

    abstract val updateFlags: MutableList<out UpdateType>

    abstract class UpdateType(internal val mask: Int)

    enum class MovementUpdateType { TELEPORT, RUN, WALK, STAY }
}