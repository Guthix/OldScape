package io.guthix.oldscape.server.gameframe

import io.guthix.oldscape.server.event.imp.PlayerInitialized

on(PlayerInitialized::class).then {
    player.setTopInterface(topInterface = 165)
    player.setSubInterface(parentInterface = 165, slot = 1, childInterface = 162, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 2, childInterface = 651, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 24, childInterface = 163, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 25, childInterface = 160, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 28, childInterface = 378, isClickable = false)
    player.setInterfaceText(parentInterface = 378, slot = 75, text = "You have a Bank PIN!")
    player.setInterfaceText(parentInterface = 378, slot = 7, text = "Welcome to OldScape emulator!")
    player.setSubInterface(parentInterface = 165, slot = 10, childInterface = 320, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 11, childInterface = 629, isClickable = true)
    player.setSubInterface(parentInterface = 629, slot = 33, childInterface = 399, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 12, childInterface = 149, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 13, childInterface = 387, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 14, childInterface = 541, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 15, childInterface = 218, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 18, childInterface = 429, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 17, childInterface = 109, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 19, childInterface = 182, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 20, childInterface = 261, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 21, childInterface = 216, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 22, childInterface = 239, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 16, childInterface = 7, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 9, childInterface = 593, isClickable = true)
    player.setSubInterface(parentInterface = 165, slot = 25, childInterface = 160, isClickable = true)
    player.gameframe = GameFrame.BLACK_SCREEN
}