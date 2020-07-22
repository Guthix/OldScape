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