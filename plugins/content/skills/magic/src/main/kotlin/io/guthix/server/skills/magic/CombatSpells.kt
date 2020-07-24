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
package io.guthix.server.skills.magic

import io.guthix.oldscape.server.blueprints.SpotAnimBlueprint

enum class CombatSpell(
    interfaceId: Int,
    interfaceSlotId: Int,
    castSound: Int,
    castAnim: Int,
    castSpotAnim: SpotAnimBlueprint,
    impactSpotAnim: SpotAnimBlueprint
) {
    WIND_STRIKE(
        interfaceId = 198,
        interfaceSlotId = 10,
        castSound = 220,
        castAnim = 711,
        castSpotAnim = SpotAnimBlueprint(id = 92, height = 124),
        impactSpotAnim = SpotAnimBlueprint(id = 90, height = 92),
    )
}

//fun genSpellBook() {
//    Enums.SPELLBOOK.forEach { index, spellBookEnum ->
//        spellBookEnum.forEach { index, objId ->
//            val objBlueprint: ObjectBlueprint = ObjectBlueprints[objId]
//
//        }
//    }
//}