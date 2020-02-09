package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.imp.LoginEvent
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.net.state.game.outp.PlayerInfoPacket
import io.guthix.oldscape.server.routine.PlayerSyncRoutine
import io.guthix.oldscape.server.routine.MapSyncRoutine
import io.guthix.oldscape.server.world.entity.character.player.interest.MapInterest
import io.guthix.oldscape.server.event.imp.PlayerInitialized

on(LoginEvent::class).then {
    var pZone = world.map.getZone(player.position) ?: throw IllegalStateException("Player location can't be null")
    player.updateFlags.add(PlayerInfoPacket.appearance)
    player.initializeInterest(world.map, world.players, pZone)
    for(skillId in 0 until 23) {
        player.updateStat(skillId, 13034431, 99)
    }
    player.updateWeight(100)
    player.updateVarbit(8119, 1)
    player.addRoutine(PlayerSyncRoutine) {
        while(true) {
            player.playerInterestSync(world.players)
            wait(ticks = 1)
        }
    }
    player.addRoutine(MapSyncRoutine) {
        while(true) {
            pZone = world.map.getZone(player.position) ?: throw IllegalStateException("Player location can't be null")
            player.mapInterest.checkReload(pZone, world.map)
            wait(ticks = 1)
        }
    }
    EventBus.schedule(PlayerInitialized(), world, player)
}