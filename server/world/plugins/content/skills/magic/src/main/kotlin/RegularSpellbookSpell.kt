import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.server.ServerContext
import io.guthix.oldscape.server.core.combat.CombatSpell
import io.guthix.oldscape.server.core.equipment.CombatProjectileType
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.SpotAnimation

val ObjTemplate.spellBookKey: Int get() = param[336] as Int

val ObjTemplate.spellRune1: Int get() = param[365] as Int

val ObjTemplate.spellRune1Amount: Int get() = param[366] as Int

val ObjTemplate.spellRune2: Int get() = param[367] as Int

val ObjTemplate.spellRune2Amount: Int get() = param[368] as Int

val ObjTemplate.spellRune3: Int get() = param[369] as Int

val ObjTemplate.spellRune3Amount: Int get() = param[370] as Int

val ObjTemplate.component: EnumConfig.Component get() = EnumConfig.Component.decode(param[596] as Int)

enum class RegularSpellbookSpell(
    private val obj: Int,
    override val castAnim: Int,
    override val castSound: Int,
    override val castSpotAnim: SpotAnimation,
    override val impactSpotAnim: SpotAnimation,
    override val projectile: ProjectileTemplate,
    override val hit: (World, Player, Character) -> Int
) : CombatSpell {
    WIND_STRIKE(
        obj = ObjIds.NULL_3273,
        castAnim = SequenceIds.SPELL_CAST_711,
        castSound = 220,
        castSpotAnim = SpotAnimation(SpotAnimIds.WIND_STRIKE_CAST_90, height = 92),
        impactSpotAnim = SpotAnimation(SpotAnimIds.WIND_STRIKE_HIT_92, height = 124),
        projectile = CombatProjectileType.MAGIC.createTemplate(91),
        hit = { _, _, _ -> 2 }
    );

    val component: EnumConfig.Component get() = ServerContext.objTemplates[obj].component

    val spellRune1: Int get() = ServerContext.objTemplates[obj].spellRune1

    val spellRune1Amount: Int get() = ServerContext.objTemplates[obj].spellRune1Amount

    val spellRune2: Int get() = ServerContext.objTemplates[obj].spellRune2

    val spellRune2Amount: Int get() = ServerContext.objTemplates[obj].spellRune2Amount

    val spellRune3: Int get() = ServerContext.objTemplates[obj].spellRune3

    val spellRune3Amount: Int get() = ServerContext.objTemplates[obj].spellRune3Amount
}