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
package io.guthix.oldscape.server

import io.guthix.js5.Js5Archive
import io.guthix.oldscape.cache.config.*
import io.guthix.oldscape.server.template.*
import mu.KLogging

object ServerContext : KLogging() {
    lateinit var enumTemplates: TemplateRepository<EnumTemplate<Any, Any>>
    lateinit var inventoryTemplates: TemplateRepository<InventoryTemplate>
    lateinit var locTemplates: TemplateRepository<LocTemplate>
    lateinit var npcTemplates: TemplateRepository<NpcTemplate>
    lateinit var objTemplates: TemplateRepository<ObjTemplate>
    lateinit var sequenceTemplates: TemplateRepository<SequenceTemplate>
    lateinit var spotAnimTemplates: TemplateRepository<SpotAnimTemplate>
    lateinit var varbitTemplates: TemplateRepository<VarbitTemplate>
    lateinit var hitbarTemplates: TemplateRepository<HitBarTemplate>

    internal fun load(archive: Js5Archive) {
        enumTemplates = TemplateRepository.of(
            EnumConfig.load(archive.readGroup(EnumConfig.id)),
            ::EnumTemplate
        )
        inventoryTemplates = TemplateRepository.of(
            InventoryConfig.load(archive.readGroup(InventoryConfig.id)),
            ::InventoryTemplate
        )
        locTemplates = TemplateRepository.of(
            LocConfig.load(archive.readGroup(LocConfig.id)),
            ::LocTemplate
        )
        npcTemplates = TemplateRepository.of(
            NpcConfig.load(archive.readGroup(NpcConfig.id)),
            ::NpcTemplate
        )
        objTemplates = TemplateRepository.of(
            ObjConfig.load(archive.readGroup(ObjConfig.id)),
            ::ObjTemplate
        )
        sequenceTemplates = TemplateRepository.of(
            SequenceConfig.load(archive.readGroup(SequenceConfig.id)),
            ::SequenceTemplate
        )
        spotAnimTemplates = TemplateRepository.of(
            SpotAnimConfig.load(archive.readGroup(SpotAnimConfig.id)),
            ::SpotAnimTemplate
        )
        varbitTemplates = TemplateRepository.of(
            VarbitConfig.load(archive.readGroup(VarbitConfig.id)),
            ::VarbitTemplate
        )
        hitbarTemplates = TemplateRepository.of(
            HitBarConfig.load(archive.readGroup(HitBarConfig.id)),
            ::HitBarTemplate
        )
    }
}