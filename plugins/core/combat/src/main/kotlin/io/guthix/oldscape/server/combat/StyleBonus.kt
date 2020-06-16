/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.blueprints.AttackStyle
import io.guthix.oldscape.server.blueprints.StyleBonus

internal fun StyleBonus.findMeleeBonus(attackStyle: AttackStyle): Int = when (attackStyle) {
    AttackStyle.STAB -> stab
    AttackStyle.SLASH -> slash
    AttackStyle.CRUSH -> crush
    else -> throw IllegalCallerException("Attack style must be a melee style.")
}

internal fun StyleBonus.findBonus(attackStyle: AttackStyle): Int = when (attackStyle) {
    AttackStyle.STAB -> stab
    AttackStyle.SLASH -> slash
    AttackStyle.CRUSH -> crush
    AttackStyle.RANGED -> range
    AttackStyle.MAGIC -> magic
    AttackStyle.NONE -> throw IllegalCallerException("Can't attack without selecting attack style.")
}
