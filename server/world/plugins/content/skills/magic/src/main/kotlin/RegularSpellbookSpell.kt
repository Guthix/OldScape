package io.guthix.oldscape.server.content.skills.magic

import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.server.template.*

val ObjTemplate.spellBookKey: Int? get() = param[336] as Int?

val ObjTemplate.spellRune1: Int? get() = param[365] as Int?

val ObjTemplate.spellRune1Amount: Int? get() = param[366] as Int?

val ObjTemplate.spellRune2: Int? get() = param[367] as Int?

val ObjTemplate.spellRune2Amount: Int? get() = param[368] as Int?

val ObjTemplate.spellRune3: Int? get() = param[369] as Int?

val ObjTemplate.spellRune3Amount: Int? get() = param[370] as Int?

val ObjTemplate.component: EnumConfig.Component get() = EnumConfig.Component.decode(param[596] as Int)