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
package io.guthix.oldscape.cache

import io.guthix.js5.Js5Archive
import io.guthix.oldscape.cache.config.AreaConfig
import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.cache.config.HitBarConfig
import io.guthix.oldscape.cache.config.HitMarkConfig
import io.guthix.oldscape.cache.config.IdentKitConfig
import io.guthix.oldscape.cache.config.InventoryConfig
import io.guthix.oldscape.cache.config.LocationConfig
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.cache.config.OverlayConfig
import io.guthix.oldscape.cache.config.ParamConfig
import io.guthix.oldscape.cache.config.SequenceConfig
import io.guthix.oldscape.cache.config.SpotAnimConfig
import io.guthix.oldscape.cache.config.StructConfig
import io.guthix.oldscape.cache.config.UnderlayConfig
import io.guthix.oldscape.cache.config.VarClientConfig
import io.guthix.oldscape.cache.config.VarPlayerConfig
import io.guthix.oldscape.cache.config.VarbitConfig

public class ConfigArchive(
    public val areaConfigs: Map<Int, AreaConfig>,
    public val enumConfigs: Map<Int, EnumConfig<Any, Any>>,
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