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
import io.guthix.oldscape.cache.config.*

class ConfigArchive(
    val areaConfigs: Map<Int, AreaConfig>,
    val enumConfigs: Map<Int, EnumConfig<Any, Any>>,
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
        const val id: Int = 2

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