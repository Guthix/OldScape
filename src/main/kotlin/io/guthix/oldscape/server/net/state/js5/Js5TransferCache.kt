package io.guthix.oldscape.server.net.state.js5

import io.guthix.cache.js5.Js5ArchiveSettings
import io.guthix.cache.js5.Js5ArchiveValidator
import io.guthix.cache.js5.Js5CacheValidator
import io.guthix.cache.js5.container.Js5Container
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.cache.js5.util.crc
import io.guthix.cache.js5.util.whirlPoolHash
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlin.math.ceil

class Js5TransferCache(val containers: Map<Int, Map<Int, ByteBuf>>) {
    companion object {
        private const val SECTOR_SIZE = 512
        private const val HEADER_SIZE = Byte.SIZE_BYTES + Short.SIZE_BYTES
        private const val BYTES_AFTER_HEADER = SECTOR_SIZE - HEADER_SIZE
        private const val BYTES_AFTER_BLOCK = SECTOR_SIZE - 1
        private const val BLOCK_HEADER = 255

        fun create(diskStore: Js5DiskStore): Js5TransferCache {
            val containers = mutableMapOf<Int, MutableMap<Int, ByteBuf>>()
            val archiveSettingsContainers = containers.getOrElse(Js5DiskStore.MASTER_INDEX) {
                mutableMapOf()
            }
            val archiveSettingsMap = mutableMapOf<Int, Js5ArchiveSettings>()
            for(archiveId in 0 until diskStore.archiveCount) {
                val archiveContainers = containers.getOrElse(archiveId) { mutableMapOf() }
                val aSettingsData = diskStore.read(diskStore.masterIdxFile, archiveId)
                archiveSettingsContainers[archiveId] = createPacket(Js5DiskStore.MASTER_INDEX, archiveId, aSettingsData)
                val archiveSettings = Js5ArchiveSettings.decode(Js5Container.decode(aSettingsData))
                archiveSettingsMap[archiveId] = archiveSettings
                val archiveIdxFile = diskStore.openArchiveIdxFile(archiveId)
                archiveSettings.groupSettings.forEach { (groupId,  _) ->
                    val data = diskStore.read(archiveIdxFile, groupId)
                    archiveContainers[groupId] = createPacket(archiveId, groupId, data)
                }
            }
            val archiveValidators = mutableListOf<Js5ArchiveValidator>()
            for((archiveId, archiveSettings) in archiveSettingsMap) {
                val data = archiveSettingsContainers[archiveId] ?: throw IllegalStateException(
                    "Can not find archive settings data for archive $archiveId"
                )
                val uncompressedSize = archiveSettings.groupSettings.values.sumBy {
                    it.sizes?.uncompressed ?: 0
                }
                archiveValidators.add(
                    Js5ArchiveValidator(
                        data.crc(), archiveSettings.version ?: 0, archiveSettings.groupSettings.size,
                        uncompressedSize, data.whirlPoolHash()
                    )
                )
            }
            val validatorData = Js5CacheValidator(archiveValidators.toTypedArray()).encode()
            archiveSettingsContainers[Js5DiskStore.MASTER_INDEX] = createPacket(
                Js5DiskStore.MASTER_INDEX, Js5DiskStore.MASTER_INDEX, validatorData
            )
            return Js5TransferCache(containers)
        }

        private fun createPacket(indexFileId: Int, containerId: Int, data: ByteBuf): ByteBuf {
            val dataSize = data.writerIndex()
            val packetBuf = Unpooled.directBuffer(HEADER_SIZE +
                    dataSize + ceil((dataSize - BYTES_AFTER_HEADER) / BYTES_AFTER_BLOCK.toDouble()).toInt()
            )
            packetBuf.writeByte(indexFileId)
            packetBuf.writeShort(containerId)
            val firstSectorSize = if(dataSize > BYTES_AFTER_HEADER) BYTES_AFTER_HEADER else dataSize
            packetBuf.writeBytes(data, firstSectorSize)
            while(data.isReadable) {
                val dataToRead = data.readableBytes()
                val sectorSize = if(dataToRead > BYTES_AFTER_BLOCK) BYTES_AFTER_BLOCK else dataToRead
                packetBuf.writeByte(BLOCK_HEADER)
                packetBuf.writeBytes(data, sectorSize)
            }
            return packetBuf
        }
    }
}