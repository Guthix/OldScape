/*
 * This file is part of Guthix OldScape-Cache.
 *
 * Guthix OldScape-Cache is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Cache is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Guthix OldScape-Cache. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.export.gltf

import de.javagl.jgltf.impl.v2.Asset
import de.javagl.jgltf.impl.v2.Node
import de.javagl.jgltf.impl.v2.Scene

val jagexAsset: Asset by lazy {
    Asset().apply {
        version = "2.0"
        copyright = "Jagex Ltd."
    }
}

val singleNodeScene: Scene by lazy {
    Scene().apply {
        addNodes(0)
    }
}

val singleMeshNode: Node by lazy {
    Node().apply {
        mesh = 0
    }
}