/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.event

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player

data class KeyboardPressEvent(
    val keyPresses: List<KeyPress>,
    override val player: Player,
    override val world: World
) : PlayerGameEvent(player, world)

class KeyPress(val key: KeyboardKey, val interval: Int)

enum class KeyboardKey(val opcode: Int) {
    F1(1),
    F2(2),
    F3(3),
    F4(4),
    F5(5),
    F6(6),
    F7(7),
    F8(8),
    F9(9),
    F10(10),
    F11(11),
    F12(12),
    ESQ(13),
    ONE(16),
    TWO(17),
    THREE(18),
    FOUR(19),
    FIVE(20),
    SIX(21),
    SEVEN(22),
    EIGHT(23),
    NINE(24),
    ZERO(25),
    DASH(26),
    PLUS(27),
    Q(32),
    W(33),
    E(34),
    R(35),
    T(36),
    Y(37),
    U(38),
    I(39),
    O(40),
    P(41),
    CURLOPEN(42),
    CURLCLOSE(43),
    A(48),
    S(49),
    D(50),
    F(51),
    G(52),
    H(53),
    J(54),
    K(55),
    L(56),
    COLON(57),
    Z(64),
    X(65),
    C(66),
    V(67),
    B(68),
    N(69),
    M(70),
    LESSTHAN(71),
    GREATERTHAN(72),
    QUESTIONMARK(73),
    PIPE(74),
    TAB(80),
    SHIFT(81),
    CTRL(82),
    SPACEBAR(83),
    ENTER(84),
    BACKSPACE(85),
    ALT(86),
    PLUS_PAD(87),
    MINUS(88),
    MULTIPLY(89),
    DIVIDE(90),
    FIVE_PAD(91),
    ARROW_LEFT(96),
    ARROW_RIGHT(97),
    ARROW_UP(98),
    ARROW_DOWN(99),
    INS(100),
    DELETE(101),
    HOME(102),
    END(103),
    PG_UP(104),
    PG_DN(105),
    UNKNOWN(-1);

    companion object {
        fun get(opcode: Int): KeyboardKey = values().find { it.opcode == opcode } ?: UNKNOWN
    }
}