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
package io.guthix.oldscape.server.buildsrc

import io.guthix.cache.js5.Js5ArchiveSettings
import io.guthix.cache.js5.Js5ArchiveValidator
import io.guthix.cache.js5.Js5CacheValidator
import io.guthix.cache.js5.container.Js5Container
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.cache.js5.util.crc
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.math.ceil

open class CompileCacheTask @Inject constructor(val ds: Js5DiskStore) : DefaultTask() {
    val buildDir = Path.of("${project.buildDir.path}\\resources\\main\\cache")

    @TaskAction
    fun execute() {
        if(!Files.exists(buildDir)) Files.createDirectories(buildDir)
        val masterDir = buildDir.resolve(Js5DiskStore.MASTER_INDEX.toString())
        if(!Files.isDirectory(masterDir)) Files.createDirectory(masterDir)
        var rebuildValidator = false
        val archiveSettingsData = mutableMapOf<Int, ByteBuf>()
        val archiveSettings = mutableMapOf<Int, Js5ArchiveSettings>()
        for(archiveId in 0 until ds.archiveCount) { // update archive settings and data
            val archiveIdx = ds.openArchiveIdxFile(archiveId)
            val archiveDir = buildDir.resolve(archiveId.toString())
            if(!Files.isDirectory(archiveDir)) Files.createDirectory(archiveDir)
            val readSettingsData = ds.read(ds.masterIdxFile, archiveId)
            val readSettings = Js5ArchiveSettings.decode(Js5Container.decode(readSettingsData))
            readSettingsData.readerIndex(0)
            val readSettingsPacket = createPacket(Js5DiskStore.MASTER_INDEX, archiveId, readSettingsData)
            readSettingsData.readerIndex(0)
            val settingsFile = masterDir.resolve(archiveId.toString())
            archiveSettingsData[archiveId] = readSettingsData
            archiveSettings[archiveId]= readSettings
            if(Files.exists(settingsFile)) {
                val storedSettingsPacket = Unpooled.wrappedBuffer(
                    Files.readAllBytes(settingsFile)
                )
                if(readSettingsPacket != storedSettingsPacket) {
                    rebuildValidator = true
                    val storedSettings = Js5ArchiveSettings.decode(
                        Js5Container.decode(unpackPacket(storedSettingsPacket))
                    )
                    for((groupId, rGroupSettings) in readSettings.groupSettings) {
                        val sGroupSettings = storedSettings.groupSettings[groupId]
                        val groupFile = archiveDir.resolve(groupId.toString())
                        if(sGroupSettings == null) { // create new file
                            val packet = createPacket(archiveId, groupId, ds.read(archiveIdx, groupId))
                            writePacket(groupFile, packet)
                            logger.info("Creating archive $archiveId group $groupId")
                        } else { // check if file is still up to date
                            if(rGroupSettings.crc != sGroupSettings.crc) {
                                replacePacket(groupFile, createPacket(archiveId, groupId, ds.read(archiveIdx, groupId)))
                                logger.info("Updating archive $archiveId group $groupId")
                            }
                        }
                    }
                    replacePacket(settingsFile, readSettingsPacket)
                    logger.info("Creating settings for $archiveId")
                }
            } else {
                rebuildValidator = true
                Files.createFile(settingsFile)
                Files.write(settingsFile, readSettingsPacket.array())
                for((groupId, _) in readSettings.groupSettings) {
                    val groupFile = archiveDir.resolve(groupId.toString())
                    val packet = createPacket(archiveId, groupId, ds.read(archiveIdx, groupId))
                    Files.createFile(groupFile)
                    Files.write(groupFile, packet.array())
                    logger.info("Creating archive $archiveId group $groupId")
                }
            }
        }
        if(rebuildValidator) { // update validator if needed
            writeValidator(archiveSettingsData, archiveSettings, masterDir)
        }
    }

    private fun replacePacket(file: Path, packet: ByteBuf) {
        Files.deleteIfExists(file)
        writePacket(file, packet)
    }

    private fun writePacket(file: Path, packet: ByteBuf) {
        Files.createFile(file)
        Files.write(file, packet.array())
    }

    private fun writeValidator(aSettingsData: Map<Int, ByteBuf>, aSettings: Map<Int, Js5ArchiveSettings>, masterDir: Path) {
        val archiveValidators = mutableListOf<Js5ArchiveValidator>()
        for((archiveId, settings) in aSettings) {
            val data = aSettingsData[archiveId] ?: throw IllegalStateException(
                    "Could not find archive data for archive $archiveId."
            )
            val heapBuf = Unpooled.copiedBuffer(data)
            val validator = Js5ArchiveValidator(heapBuf.crc(), settings.version ?: 0, null, null, null)
            archiveValidators.add(validator)
        }
        val validatorData = Js5CacheValidator(archiveValidators.toTypedArray()).encode()
        val validatorPacket = createPacket(
            Js5DiskStore.MASTER_INDEX, Js5DiskStore.MASTER_INDEX, Js5Container(data = validatorData).encode()
        )
        val validatorFile = masterDir.resolve(Js5DiskStore.MASTER_INDEX.toString())
        replacePacket(validatorFile, validatorPacket)
    }

    private fun createPacket(indexFileId: Int, containerId: Int, data: ByteBuf): ByteBuf {
        val dataSize = data.writerIndex()
        val packetBuf = Unpooled.buffer(HEADER_SIZE + dataSize + ceil(
            (dataSize - BYTES_AFTER_HEADER) / BYTES_AFTER_BLOCK.toDouble()
        ).toInt())
        packetBuf.writeByte(indexFileId)
        packetBuf.writeShort(containerId)
        val firstSectorSize = if(data.readableBytes() > BYTES_AFTER_HEADER) BYTES_AFTER_HEADER else data.readableBytes()
        packetBuf.writeBytes(data, firstSectorSize)
        while(data.isReadable) {
            val dataToRead = data.readableBytes()
            val sectorSize = if(dataToRead > BYTES_AFTER_BLOCK) BYTES_AFTER_BLOCK else dataToRead
            packetBuf.writeByte(BLOCK_HEADER)
            packetBuf.writeBytes(data, sectorSize)
        }
        return packetBuf
    }

    private fun unpackPacket(data: ByteBuf) : ByteBuf {
        data.readUnsignedByte() // index file id
        data.readUnsignedShort() // container id
        val dataLeft = data.writerIndex() - data.readerIndex()
        val resultSize = if(dataLeft <= BYTES_AFTER_HEADER) {
            dataLeft
        } else {
            dataLeft - ceil((dataLeft - BYTES_AFTER_HEADER) / SECTOR_SIZE.toDouble()).toInt()
        }
        val resultBuffer = Unpooled.buffer(resultSize)
        val headDataSize = if(data.readableBytes() > BYTES_AFTER_HEADER) BYTES_AFTER_HEADER else data.readableBytes()
        data.readBytes(resultBuffer, headDataSize)
        while(data.isReadable) {
            check(data.readUnsignedByte().toInt() == 0xFF) { "First block byte should be equal to 0xFF." }
            val blockDataSize = if(data.readableBytes() > BYTES_AFTER_BLOCK) BYTES_AFTER_BLOCK else data.readableBytes()
            data.readBytes(resultBuffer, blockDataSize)
        }
        return resultBuffer
    }

    companion object {
        private const val SECTOR_SIZE = 512
        private const val HEADER_SIZE = Byte.SIZE_BYTES + Short.SIZE_BYTES
        private const val BYTES_AFTER_HEADER = SECTOR_SIZE - HEADER_SIZE
        private const val BYTES_AFTER_BLOCK = SECTOR_SIZE - 1
        private const val BLOCK_HEADER = 255
    }

}