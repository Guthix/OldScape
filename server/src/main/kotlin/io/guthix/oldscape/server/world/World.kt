/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.world

import io.guthix.oldscape.cache.map.MapDefinition
import io.guthix.oldscape.cache.map.MapLocDefinition
import io.guthix.oldscape.cache.map.MapSquareDefinition
import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.db.KotlinClass
import io.guthix.oldscape.server.db.PlayerPropertiesTable
import io.guthix.oldscape.server.db.PlayerTable
import io.guthix.oldscape.server.db.upsert
import io.guthix.oldscape.server.event.*
import io.guthix.oldscape.server.net.StatusEncoder
import io.guthix.oldscape.server.net.StatusResponse
import io.guthix.oldscape.server.net.game.GameDecoder
import io.guthix.oldscape.server.net.game.GameEncoder
import io.guthix.oldscape.server.net.game.GameHandler
import io.guthix.oldscape.server.net.login.*
import io.guthix.oldscape.server.plugin.EventHandler
import io.guthix.oldscape.server.task.Task
import io.guthix.oldscape.server.task.TaskHolder
import io.guthix.oldscape.server.task.TaskType
import io.guthix.oldscape.server.template.ProjectileTemplate
import io.guthix.oldscape.server.world.entity.*
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.Zone
import io.guthix.oldscape.server.world.map.dim.*
import io.netty.util.concurrent.DefaultPromise
import io.netty.util.concurrent.ImmediateEventExecutor
import io.netty.util.concurrent.PromiseCombiner
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import mu.KLogging
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingDeque
import kotlin.reflect.KProperty
import kotlin.reflect.full.starProjectedType

class World internal constructor(
    internal val map: Array<Array<Array<Zone?>>>,
    val xteas: Map<Int, IntArray>
) : TimerTask(), TaskHolder, EventHolder, PropertyHolder {
    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()

    override val events: LinkedBlockingDeque<EventHandler<Event>> = LinkedBlockingDeque()

    override val tasks: MutableMap<TaskType, MutableSet<Task>> = mutableMapOf()

    internal val loginQueue = ConcurrentLinkedQueue<LoginRequest>()

    internal val logoutQueue = ConcurrentLinkedQueue<Player>()

    val players: PlayerList = PlayerList(MAX_PLAYERS)

    val npcs: NpcList = NpcList(MAX_NPCS)

    val isFull: Boolean get() = players.size + loginQueue.size >= MAX_PLAYERS

    override fun run() {
        processInEvents()
        processLogins()
        processNpcTasks()
        proccessNpcMovement()
        processPlayerTasks()
        proccessPlayerMovement()
        synchronizeInterest()
        processLogouts()
        postProcess()
    }

    private fun processInEvents() {
        while (true) {
            while (events.isNotEmpty()) events.poll().handle()
            val resumed = tasks.values.flatMap { routineList -> routineList.toList().map(Task::run) } // TODO optimize
            if (resumed.all { !it } && events.isEmpty()) break // TODO add live lock detection
        }
    }

    private fun processLogins() {
        main@ while (loginQueue.isNotEmpty()) {
            val request = loginQueue.poll()
            val playerDbData = try {
                transaction {
                    val playerTableRow = PlayerTable.select {
                        PlayerTable.username eq request.username
                    }.single()
                    val playerUid = playerTableRow[PlayerTable.id]
                    val properties = PlayerPropertiesTable.select {
                        PlayerPropertiesTable.playerId eq playerUid
                    }
                    playerUid to properties.map {
                        val jsonData = it[PlayerPropertiesTable.property]
                        val type = KotlinClass.fromName(it[PlayerPropertiesTable.type]).starProjectedType
                        it[PlayerPropertiesTable.name] to Json.decodeFromString(serializer(type), jsonData)!!
                    }.toMap().toMutableMap()
                }
            } catch (e: NoSuchElementException) {
                request.ctx.writeAndFlush(StatusResponse.INVALID_CREDENTIALS)
                break@main
            }
            request.ctx.write(StatusResponse.NORMAL)
            request.ctx.pipeline().replace(
                StatusEncoder::class.qualifiedName, LoginEncoder::class.qualifiedName, LoginEncoder()
            )
            val player = players.create(playerDbData.first, playerDbData.second, request)
            request.ctx.writeAndFlush(LoginResponse(player.index, player.rights))
            request.ctx.pipeline().replace(LoginDecoder::class.qualifiedName, GameDecoder::class.qualifiedName,
                GameDecoder(request.isaacPair.decodeGen, player, this)
            )
            request.ctx.pipeline().replace(LoginHandler::class.qualifiedName, GameHandler::class.qualifiedName,
                GameHandler(player)
            )
            request.ctx.pipeline().replace(LoginEncoder::class.qualifiedName, GameEncoder::class.qualifiedName,
                GameEncoder(request.isaacPair.encodeGen)
            )
            EventBus.schedule(LoginEvent(player, this))
        }
    }

    fun addNpc(id: Int, tile: Tile): Npc {
        val npc = npcs.create(id, tile)
        EventBus.schedule(NpcSpawnedEvent(npc, this))
        return npc
    }

    fun stagePlayerLogout(player: Player) {
        player.stageLogout()
        logoutQueue.add(player)
    }

    private fun processLogouts() {
        while (logoutQueue.isNotEmpty()) {
            val player = logoutQueue.poll()
            transaction {
                player.persistentProperties.forEach { (key, value) ->
                    val projType = value::class.starProjectedType
                    PlayerPropertiesTable.upsert(PlayerPropertiesTable.name) {
                        it[playerId] = player.uid
                        it[name] = key
                        it[type] = "$projType"
                        it[property] = Json.encodeToString(serializer(projType), value)
                    }
                }
            }
            players.remove(player)
        }
    }

    private fun processNpcTasks() {
        for (npc in npcs) npc.processTasks()
    }

    private fun processPlayerTasks() {
        for (player in players) player.processTasks()
    }

    private fun proccessNpcMovement() {
        for (npc in npcs) npc.move()
    }

    private fun proccessPlayerMovement() {
        for (player in players) player.move()
    }

    private fun synchronizeInterest() {
        val futures = PromiseCombiner(ImmediateEventExecutor.INSTANCE)
        players.forEach { it.synchronize(this).forEach(futures::add) }
        futures.finish(DefaultPromise<Void>(ImmediateEventExecutor.INSTANCE).addListener {
            if (it.isSuccess) {
                for (player in players) player.postProcess()
                for (npc in npcs) npc.postProcess()
            } else {
                throw it.cause()
            }
        })
    }

    fun getCollision(tile: Tile): Int = getCollision(tile.floor, tile.x, tile.y)

    fun getCollision(floor: FloorUnit, x: TileUnit, y: TileUnit): Int = getZone(floor, x, y)?.masks
        ?.get(x.relativeZone.value)?.get(y.relativeZone.value) ?: Collision.MASK_TERRAIN_BLOCK

    internal fun getZone(tile: Tile): Zone? = map[tile.floor.value][tile.x.inZones.value][tile.y.inZones.value]

    internal fun getZone(floor: FloorUnit, x: TileUnit, y: TileUnit): Zone? =
        map[floor.value][x.inZones.value][y.inZones.value]

    internal fun getZone(floor: FloorUnit, x: ZoneUnit, y: ZoneUnit): Zone? = map[floor.value][x.value][y.value]

    fun getLoc(id: Int, floor: FloorUnit, x: TileUnit, y: TileUnit): Loc? = getZone(floor, x, y)?.getLoc(
        id, x.relativeZone, y.relativeZone
    )

    fun addObject(id: Int, amount: Int, tile: Tile): Obj {
        val obj = Obj(id, amount)
        getZone(tile)?.addObject(tile, obj)
        return obj
    }

    fun addObject(obj: Obj, tile: Tile): Unit? = getZone(tile)?.addObject(tile, obj)

    fun removeObject(id: Int, tile: Tile): Obj? = getZone(tile)?.removeObject(tile, id)

    fun addLoc(loc: Loc): Loc {
        getZone(loc.pos)?.addLoc(loc)
        addLocCollision(loc)
        return loc
    }

    fun delLoc(loc: Loc) {
        getZone(loc.pos)?.delLoc(loc)
        delLocCollision(loc)
    }

    fun addProjectile(template: ProjectileTemplate, start: Tile, target: Character): Projectile {
        val projectile = Projectile(template, start, target)
        getZone(start)?.addProjectile(projectile)
        return projectile
    }

    companion object : KLogging() {
        const val MAX_PLAYERS: Int = 2048

        const val MAX_NPCS: Int = 32768

        fun fromMap(mapsquares: Map<Int, MapSquareDefinition>, xteas: Map<Int, IntArray>): World {
            val map = Array(4) { Array(2048) { arrayOfNulls<Zone?>(2048) } }
            mapsquares.forEach { (_, def) ->
                (0.floors until MapsquareUnit.FLOOR_COUNT).forEach { floor ->
                    (0.zones until MapsquareUnit.SIZE_ZONE).map { def.x.mapsquares.inZones + it }.forEach { x ->
                        (0.zones until MapsquareUnit.SIZE_ZONE).map { def.y.mapsquares.inZones + it }.forEach {
                            map[floor.value][x.value][it.value] = Zone(floor, x, it)
                        }
                    }
                }
            }
            val world = World(map, xteas)
            mapsquares.forEach { (_, def) ->
                world.addTerrain(def.x.mapsquares, def.y.mapsquares, def.mapDefinition)
                def.locationDefinitions.forEach { world.addLocByDef(def.x.mapsquares, def.y.mapsquares, it) }
            }
            val zoneCount = map.sumBy { floor -> floor.sumBy { yZones -> yZones.count { it != null } } }
            logger.info { "Loaded $zoneCount zones" }
            return world
        }

        private fun World.addTerrain(msX: MapsquareUnit, msY: MapsquareUnit, def: MapDefinition) {
            def.renderRules.forEachIndexed { floor, floorRenderRules ->
                floorRenderRules.forEachIndexed { x, verticalRenderRules ->
                    verticalRenderRules.forEachIndexed { y, currentRule ->
                        var z = floor
                        if (currentRule.toInt() and MapDefinition.BLOCKED_TILE_MASK.toInt() == 1) {
                            if (def.renderRules[1][x][y].toInt() and MapDefinition.BRIDGE_TILE_MASK.toInt() == 2) {
                                z--
                            }
                            if (z >= 0) {
                                getZone(z.floors, msX.inTiles + x.tiles, msY.inTiles + y.tiles)?.addCollision(
                                    x.tiles.relativeZone, y.tiles.relativeZone, Collision.MASK_TERRAIN_BLOCK
                                )
                            }
                        }
                    }
                }
            }
        }

        private fun World.addLocByDef(msX: MapsquareUnit, msY: MapsquareUnit, locDef: MapLocDefinition)
            = addStaticLoc(
            Loc(locDef.id,
                locDef.type,
                Tile(locDef.floor.floors, msX.inTiles + locDef.localX.tiles, msY.inTiles + locDef.localY.tiles),
                locDef.orientation
            )
        )

        private fun World.addStaticLoc(loc: Loc) {
            getZone(loc.pos)?.addStaticLoc(loc)
            addLocCollision(loc)
        }

        private fun Zone.addStaticLoc(loc: Loc) {
            staticLocs[loc.mapKey] = loc
        }
    }
}