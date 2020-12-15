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
package io.guthix.oldscape.server.combat.player

import io.guthix.oldscape.server.event.ButtonClickEvent
import io.guthix.oldscape.server.event.PlayerInitialized
import io.guthix.oldscape.server.template.VarpIds

on(PlayerInitialized::class).then {
    player.updateVarp(VarpIds.AUTO_RETALIATE_172, if(player.autoRetaliate) 0 else 1)
}

on(ButtonClickEvent::class).where { interfaceId == 593 && buttonId == 30 }.then {
    player.autoRetaliate = !player.autoRetaliate
    player.updateVarp(VarpIds.AUTO_RETALIATE_172, if(player.autoRetaliate) 0 else 1)
}