package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.net.state.login.LoginRequest
import io.guthix.oldscape.server.world.entity.player.PlayerList
import java.util.*
import java.util.concurrent.*

class World : TimerTask() {
    internal val loginQueue = SynchronousQueue<LoginRequest>()

    internal val players = PlayerList(MAX_PLAYERS)

    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun processLogins() {
        while(loginQueue.peek() != null) {
            val request = loginQueue.poll()
            players.add(request)
        }
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}