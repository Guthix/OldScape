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
package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.LoginEvent
import io.guthix.oldscape.server.event.PlayerInitialized
import io.guthix.oldscape.server.template.VarbitTemplates

on(LoginEvent::class).then {
    player.initialize(world)
    player.updateVarbit(VarbitTemplates.CHAT_ENABLE_8119, value = 1) // enable chat
    player.updateVarp(id = 1306, value = 2) // set attack option for npcs (0, 1, 2, 3)
    player.updateVarp(id = 1107, value = 2) // set attack option for players (0, 1, 2, 3)
    player.senGameMessage("Welcome to OldScape Emulator!")
    EventBus.schedule(PlayerInitialized(player, world))
}