package io.guthix.oldscape.server

import io.guthix.cache.js5.Js5ArchiveSettings
import io.guthix.cache.js5.Js5Group
import io.guthix.cache.js5.Js5GroupData
import io.guthix.cache.js5.Js5GroupSettings
import io.guthix.cache.js5.container.Js5Container
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.EnumConfig
import io.netty.buffer.ByteBuf
import mu.KotlinLogging
import java.io.FileNotFoundException

private val logger = KotlinLogging.logger {  }

object Cache {
    val data: MutableMap<Int, MutableMap<Int, Js5RawGroup>> = mutableMapOf()

    fun getGroup(archiveId: Int, gropuId: Int) : Js5Group {
        val cachedGroup = data[ConfigArchive.id]?.get(EnumConfig.id) ?: throw FileNotFoundException(
            "Could not find group $gropuId in archive $archiveId."
        )
        val container = Js5Container.decode(cachedGroup.data)
        val groupData = Js5GroupData.decode(container, cachedGroup.settings.fileSettings.size)
        return Js5Group.create(groupData, cachedGroup.settings)
    }

    fun load(store: Js5DiskStore) {
        val archiveSettings = mutableMapOf<Int, Js5ArchiveSettings>()
        for(archiveId in 0 until store.archiveCount) {
            val settings = Js5ArchiveSettings.decode(Js5Container.decode(store.read(store.masterIdxFile, archiveId)))
            archiveSettings[archiveId] = settings
        }
        archiveSettings.forEach { (archiveId, archiveSettings) ->
            val archiveFile = store.openArchiveIdxFile(archiveId)
            archiveSettings.groupSettings.forEach { (groupId, groupSettings) ->
                val archiveData = data.getOrPut(archiveId, { mutableMapOf() })
                archiveData[groupId] = Js5RawGroup(groupSettings, store.read(archiveFile, groupId))
            }
        }
        logger.info { "Loaded ${data.keys.size} cache archives" }
    }
}

class Js5RawGroup(val settings: Js5GroupSettings, val data: ByteBuf)