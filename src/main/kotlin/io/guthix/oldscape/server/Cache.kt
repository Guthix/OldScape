/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server

import io.guthix.cache.js5.*
import io.guthix.cache.js5.container.Js5Container
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.cache.js5.util.XTEA_ZERO_KEY
import io.guthix.cache.js5.util.crc
import io.guthix.cache.js5.util.whirlPoolHash
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.EnumConfig
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import mu.KotlinLogging
import java.io.FileNotFoundException

private val logger = KotlinLogging.logger {  }

object Cache {
    private val groupData: MutableMap<Int, MutableMap<Int, Js5RawGroup>> = mutableMapOf()

    private val settingsData: MutableMap<Int, ByteBuf> = mutableMapOf()

    fun getRawSettings(archiveId: Int): ByteBuf {
        return settingsData[archiveId] ?: throw FileNotFoundException(
            "Could not find settings for archive $archiveId."
        )
    }

    fun getRawGroup(archiveId: Int, groupId: Int): Js5RawGroup {
        return groupData[archiveId]?.get(groupId) ?: throw FileNotFoundException(
            "Could not find group $groupId in archive $archiveId."
        )
    }

    fun getGroup(archiveId: Int, groupId: Int) : Js5Group {
        val cachedGroup = getRawGroup(archiveId, groupId)
        val container = Js5Container.decode(cachedGroup.data.duplicate())
        val groupData = Js5GroupData.decode(container, cachedGroup.settings.fileSettings.size)
        return Js5Group.create(groupData, cachedGroup.settings)
    }

    fun load(store: Js5DiskStore) {
        val archiveSettings = mutableMapOf<Int, Js5ArchiveSettings>()
        for(archiveId in 0 until store.archiveCount) {
            val data = store.read(store.masterIdxFile, archiveId)
            settingsData[archiveId] = data
            val settings = Js5ArchiveSettings.decode(Js5Container.decode(data.duplicate()))
            archiveSettings[archiveId] = settings
        }
        archiveSettings.forEach { (archiveId, archiveSettings) ->
            val archiveFile = store.openArchiveIdxFile(archiveId)
            archiveSettings.groupSettings.forEach { (groupId, groupSettings) ->
                val archiveData = groupData.getOrPut(archiveId, { mutableMapOf() })
                archiveData[groupId] = Js5RawGroup(groupSettings, store.read(archiveFile, groupId))
            }
        }
        val archiveValidators = settingsData.keys.map { archiveId ->
            val data = store.read(store.masterIdxFile, archiveId)
            val settings = Js5ArchiveSettings.decode(Js5Container.decode(data.duplicate()))
            Js5ArchiveValidator(data.copy().crc(), settings.version ?: 0, null, null, null)
        }
        settingsData[Js5DiskStore.MASTER_INDEX] = Js5Container(
            Js5CacheValidator(archiveValidators.toTypedArray()).encode()
        ).encode()
        logger.info { "Loaded ${groupData.keys.size} cache archives" }
    }
}

class Js5RawGroup(val settings: Js5GroupSettings, val data: ByteBuf)