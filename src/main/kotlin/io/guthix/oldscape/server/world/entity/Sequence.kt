/*
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
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.api.SequenceBlueprints
import io.guthix.oldscape.server.api.SpotAnimBlueprints
import io.guthix.oldscape.server.blueprints.SpotAnimation

val SpotAnimation.sequence: Sequence? get() = SpotAnimBlueprints[id].sequenceId?.let(::Sequence)

class Sequence(id: Int) {
    private val blueprint = SequenceBlueprints[id]

    val id: Int get() = blueprint.id

    val duration: Int = blueprint.frameDuration?.sum()?.toDouble()?.div(30)?.toInt() ?: throw IllegalStateException(
        "Sequence $id has no duration."
    )
}