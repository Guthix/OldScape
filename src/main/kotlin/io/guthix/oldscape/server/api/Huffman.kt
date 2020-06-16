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
package io.guthix.oldscape.server.api

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.BinariesArchive
import io.guthix.oldscape.cache.binary.Huffman
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

object Huffman {
    private lateinit var huffman: Huffman

    fun compress(text: String): ByteArray = huffman.compress(text)

    fun decompress(comp: ByteArray, length: Int): ByteArray = huffman.decompress(comp, length)

    fun load(archive: Js5Archive) {
        huffman = BinariesArchive.load(archive).huffman
        logger.info { "Loaded huffman" }
    }
}