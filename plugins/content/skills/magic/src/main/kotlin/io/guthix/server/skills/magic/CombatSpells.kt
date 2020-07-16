/*
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
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