package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.World
import io.guthix.buffer.toBitMode
import kotlin.math.ceil

private val FIELD_TILE_SIZE = 8192

val Tile.regionBitPack get() =
    (z.value shl 16) or ((x.value / FIELD_TILE_SIZE) shl 8) or (y.value / FIELD_TILE_SIZE)

val Tile.bitpack get() = (z.value shl 28) or (x.value shl 14) or y.value

on(StartPlayerSyncEvent::class).then {
    println("Player interest")
}