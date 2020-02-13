package io.guthix.oldscape.server.world.entity.character.player

data class Appearance(
    var gender: Gender,
    var isSkulled: Boolean,
    var overheadIcon: Int,
    var apparel: Apparel,
    var animations: Animations
) {
    data class Apparel(
        var skinColor: Int,
        var head: Int,
        var chest: Int,
        var hands: Int,
        var legs: Int,
        var feet: Int,
        var weapon: Int,
        var shield: Int
    )

    data class Animations(
        var stand: Int,
        var turn: Int,
        var walk: Int,
        var turn180: Int,
        var turn90CW: Int,
        var turn90CCW: Int,
        var run: Int
    )

    enum class Gender(val opcode: Int) { MALE(0), FEMALE(1) }
}