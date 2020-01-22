/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
@file:Suppress("unused")
package io.guthix.oldscape.cache

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.config.*

data class ConfigArchive(
    val areaConfigs: Map<Int, AreaConfig>,
    val enumConfigs: Map<Int, EnumConfig>,
    val hitBarConfig: Map<Int, HitBarConfig>,
    val hitMarkConfigss: Map<Int, HitMarkConfig>,
    val identKitConfigs: Map<Int, IdentKitConfig>,
    val inventoryConfigs: Map<Int, InventoryConfig>,
    val objConfigs: Map<Int, ObjConfig>,
    val npcConfigs: Map<Int, NpcConfig>,
    val locConfigs: Map<Int, LocConfig>,
    val overlayConfigs: Map<Int, OverlayConfig>,
    val paramConfigs: Map<Int, ParamConfig>,
    val sequenceConfigs: Map<Int, SequenceConfig>,
    val spotAnimConfigs: Map<Int, SpotAnimConfig>,
    val structConfigs: Map<Int, StructConfig>,
    val underlayConfigs: Map<Int, UnderlayConfig>,
    val varbitConfigs: Map<Int, VarbitConfig>,
    val varClientConfigs: Map<Int, VarClientConfig>,
    val varPlayerConfigs: Map<Int, VarPlayerConfig>
) {
    companion object {
        const val id = 2

        fun load(archive: Js5Archive): ConfigArchive = ConfigArchive(
            AreaConfig.load(archive.readGroup(AreaConfig.id)),
            EnumConfig.load(archive.readGroup(EnumConfig.id)),
            HitBarConfig.load(archive.readGroup(HitBarConfig.id)),
            HitMarkConfig.load(archive.readGroup(HitMarkConfig.id)),
            IdentKitConfig.load(archive.readGroup(IdentKitConfig.id)),
            InventoryConfig.load(archive.readGroup(InventoryConfig.id)),
            ObjConfig.load(archive.readGroup(ObjConfig.id)),
            NpcConfig.load(archive.readGroup(NpcConfig.id)),
            LocConfig.load(archive.readGroup(LocConfig.id)),
            OverlayConfig.load(archive.readGroup(OverlayConfig.id)),
            ParamConfig.load(archive.readGroup(ParamConfig.id)),
            SequenceConfig.load(archive.readGroup(SequenceConfig.id)),
            SpotAnimConfig.load(archive.readGroup(SpotAnimConfig.id)),
            StructConfig.load(archive.readGroup(StructConfig.id)),
            UnderlayConfig.load(archive.readGroup(UnderlayConfig.id)),
            VarbitConfig.load(archive.readGroup(VarbitConfig.id)),
            VarClientConfig.load(archive.readGroup(VarClientConfig.id)),
            VarPlayerConfig.load(archive.readGroup(VarPlayerConfig.id))
        )
    }
}