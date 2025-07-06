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
package io.guthix.oldscape.cache

import io.guthix.js5.Js5Archive
import io.guthix.oldscape.cache.config.*

public class ConfigArchive(
    public val areaConfigs: Map<Int, AreaConfig>,
    public val enumConfigs: Map<Int, EnumConfig<Any, Any>>,
    public val hitBarConfig: Map<Int, HitBarConfig>,
    public val hitMarkConfigss: Map<Int, HitMarkConfig>,
    public val identKitConfigs: Map<Int, IdentKitConfig>,
    public val inventoryConfigs: Map<Int, InventoryConfig>,
    public val objConfigs: Map<Int, ObjConfig>,
    public val npcConfigs: Map<Int, NpcConfig>,
    public val locConfigs: Map<Int, LocConfig>,
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
        public const val ID: Int = 2

        public fun load(archive: Js5Archive): ConfigArchive = ConfigArchive(
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