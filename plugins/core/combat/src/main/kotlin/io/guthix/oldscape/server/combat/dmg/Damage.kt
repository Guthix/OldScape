package io.guthix.oldscape.server.combat.dmg

import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import kotlin.random.Random

private fun calcDamage(accuracy: Double, maxHit: Int): Int? = if(Random.nextInt(1) < accuracy) {
    Random.nextInt(maxHit + 1)
} else null

fun Player.calcHit(other: Player, maxHit: Int): Int? = calcDamage(accuracy(other), maxHit)

fun Player.calcHit(other: Npc, maxHit: Int): Int? = calcDamage(accuracy(other), maxHit)

fun Npc.calcHit(other: Player): Int? = calcDamage(accuracy(other), maxHit())

fun Npc.calcHit(other: Npc): Int? = calcDamage(accuracy(other), maxHit())