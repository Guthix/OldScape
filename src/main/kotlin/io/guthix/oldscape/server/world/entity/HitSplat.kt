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
package io.guthix.oldscape.server.world.entity

sealed class HitSplat(val id: Int, val damage: Int, val delay: Int)

class MissHitSplat(damage: Int, delay: Int) : HitSplat(0, damage, delay)

class DamageHitSplat(damage: Int, delay: Int) : HitSplat(1, damage, delay)

class PoisonHitSplat(damage: Int, delay: Int) : HitSplat(2, damage, delay)

class HeatHitSplat(damage: Int, delay: Int) : HitSplat(3, damage, delay)

class DiseaseHitSplat(damage: Int, delay: Int) : HitSplat(4, damage, delay)

class VenomHitSplat(damage: Int, delay: Int) : HitSplat(5, damage, delay)