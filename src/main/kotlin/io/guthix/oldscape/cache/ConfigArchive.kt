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
package io.guthix.oldscape.cache

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.config.*

public class ConfigArchive(
    public val areaConfigs: Map<Int, AreaConfig>,
    public val enumConfigs: Map<Int, EnumConfig>,
    public val hitBarConfig: Map<Int, HitBarConfig>,
    public val hitMarkConfigss: Map<Int, HitMarkConfig>,
    public val identKitConfigs: Map<Int, IdentKitConfig>,
    public val inventoryConfigs: Map<Int, InventoryConfig>,
    public val objectConfigs: Map<Int, ObjectConfig>,
    public val npcConfigs: Map<Int, NpcConfig>,
    public val locationConfigs: Map<Int, LocationConfig>,
    public val overlayConfigs: Map<Int, OverlayConfig>,
    public val paramConfigs: Map<Int, ParamConfig>,
    public val sequenceConfigs: Map<Int, SequenceConfig>,
    public val spotAnimConfigs: Map<Int, SpotAnimConfig>,
    public val structConfigs: Map<Int, StructConfig>,
    public val underlayConfigs: Map<Int, UnderlayConfig>,
    public val varbitConfigs: Map<Int, VarbitConfig>,
    public val varClientConfigs: Map<Int, VarClientConfig>,
    public val varPlayerConfigs: Map<Int, VarPlayerConfig>
) {
    public companion object {
        public const val id: Int = 2

        public fun load(archive: Js5Archive): ConfigArchive = ConfigArchive(
            AreaConfig.load(archive.readGroup(AreaConfig.id)),
            EnumConfig.load(archive.readGroup(EnumConfig.id)),
            HitBarConfig.load(archive.readGroup(HitBarConfig.id)),
            HitMarkConfig.load(archive.readGroup(HitMarkConfig.id)),
            IdentKitConfig.load(archive.readGroup(IdentKitConfig.id)),
            InventoryConfig.load(archive.readGroup(InventoryConfig.id)),
            ObjectConfig.load(archive.readGroup(ObjectConfig.id)),
            NpcConfig.load(archive.readGroup(NpcConfig.id)),
            LocationConfig.load(archive.readGroup(LocationConfig.id)),
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