/*
 * Copyright 2018-2021 Guthix
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
package io.guthix.oldscape.server.core.session

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.LoginEvent
import io.guthix.oldscape.server.event.PlayerInitialized
import io.guthix.oldscape.cache.VarbitIds
import io.guthix.oldscape.cache.VarpIds

on(LoginEvent::class).then {
    player.initialize(world)
    player.updateVarbit(VarbitIds.CHAT_ENABLE_8119, value = 1) // enable chat
    player.updateVarp(VarpIds.NPC_ATTACK_OPTION_SETTING_1306, value = 2) // (0, 1, 2, 3)
    player.updateVarp(VarpIds.PLAYER_ATTACK_OPTION_SETTING_1107, value = 2) // (0, 1, 2, 3)
    player.sendGameMessage("Welcome to OldScape Emulator!")
    EventBus.schedule(PlayerInitialized(player, world))
}