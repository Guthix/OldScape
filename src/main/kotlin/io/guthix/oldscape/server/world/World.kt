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
package io.guthix.oldscape.server.world

import io.guthix.cache.js5.Js5Archive
import io.guthix.cache.js5.util.XTEA_ZERO_KEY
import io.guthix.oldscape.cache.MapArchive
import io.guthix.oldscape.cache.xtea.MapXtea
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.net.state.game.GameDecoder
import io.guthix.oldscape.server.net.state.game.GameEncoder
import io.guthix.oldscape.server.net.state.game.GameHandler
import io.guthix.oldscape.server.net.state.login.*
import io.guthix.oldscape.server.world.entity.character.player.PlayerList
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.mapsquare.Mapsquare
import io.guthix.oldscape.server.world.mapsquare.mapsquares
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneCollision
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import java.util.*
import java.util.concurrent.*

class World : TimerTask() {
    val map: MutableMap<Int, Mapsquare> = mutableMapOf()

    internal val loginQueue = ConcurrentLinkedQueue<LoginRequest>()

    val players = PlayerList(MAX_PLAYERS)

    val isFull get(): Boolean = players.size + loginQueue.size >= MAX_PLAYERS

    fun initMap(archive: Js5Archive, xteas: List<MapXtea>): World {
        val mapArchive = MapArchive.load(archive, xteas)
        for(mapXtea in xteas) {
            val mapsquare = mapArchive.mapsquares[mapXtea.id] ?: continue
            map[mapXtea.id] = Mapsquare(mapsquare.x.mapsquares, mapsquare.y.mapsquares, mapXtea.key, this)
        }
        map.forEach { (id, mapsquare) ->
            val def = mapArchive.mapsquares[id] ?: throw IllegalStateException(
                "Could not find mapsquare definition for id $id."
            )
            mapsquare.initialize(def)
        }
        return this
    }

    fun getZone(tile: Tile) = getZone(tile.floor, tile.x, tile.y)

    fun getZone(floor: FloorUnit, x: TileUnit, y: TileUnit): Zone? = map[Mapsquare.id(x.inMapsquares, y.inMapsquares)]?.
        getZone(floor, x.relativeMapSquare, y.relativeMapSquare)

    fun addUnwalkableTile(floor: FloorUnit, x: TileUnit, y: TileUnit) {
        map[Mapsquare.id(x.inMapsquares, y.inMapsquares)]?.addUnwalkableTile(
            floor, x.relativeMapSquare, y.relativeMapSquare
        )
    }

    fun getCollisionMask(floor: FloorUnit, x: TileUnit, y: TileUnit): Int {
        return map[Mapsquare.id(x.inMapsquares, y.inMapsquares)]?.getCollisionMask(
            floor, x.relativeMapSquare, y.relativeZone
        ) ?: ZoneCollision.MASK_UNWALKABLE_TILE
    }

    override fun run() {
        processLogins()
        processPlayerEvents()
    }

    private fun processLogins() {
        while(loginQueue.isNotEmpty()) {
            val request = loginQueue.poll()
            val player= players.create(request)
            player.clientSettings = request.clientSettings
            request.ctx.writeAndFlush(LoginResponse(player.index, player.rights))
            request.ctx.pipeline().replace(LoginDecoder::class.qualifiedName, GameDecoder::class.qualifiedName,
                GameDecoder(request.isaacPair.decodeGen)
            )
            request.ctx.pipeline().replace(LoginHandler::class.qualifiedName, GameHandler::class.qualifiedName,
                GameHandler(this, player)
            )
            request.ctx.pipeline().replace(LoginEncoder::class.qualifiedName, GameEncoder::class.qualifiedName,
                GameEncoder(request.isaacPair.encodeGen)
            )
            EventBus.schedule(LoginEvent(), this, player)
        }
    }

    private fun processPlayerEvents() {
        for(player in players) player.handleEvents()
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}