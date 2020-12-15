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
package io.guthix.oldscape.server.combat.player

import io.guthix.oldscape.server.event.ObjEquipedEvent
import io.guthix.oldscape.server.event.PlayerInitialized
import io.guthix.oldscape.server.template.VarbitIds
import io.guthix.oldscape.server.template.weaponType

on(PlayerInitialized::class).then {
    player.updateVarbit(VarbitIds.ATTACK_STYLE_357, player.equipment.weapon?.weaponType?.id ?: 0)
}

on(ObjEquipedEvent::class).then {
    player.updateVarbit(VarbitIds.ATTACK_STYLE_357, player.equipment.weapon?.weaponType?.id ?: 0)
}