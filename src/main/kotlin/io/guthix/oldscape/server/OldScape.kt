package io.guthix.oldscape.server

import com.charleskorn.kaml.Yaml
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.server.net.OldScapeServer
import io.guthix.oldscape.server.net.state.js5.Js5TransferCache
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val configFile = Path.of(ServerConfig::class.java.getResource("/Config.yaml").toURI())
    val config = Yaml.default.parse(ServerConfig.serializer(), Files.readString(configFile))
    val cacheDir = Path.of(Js5TransferCache::class.java.getResource("/cache").toURI())
    val diskStore = Js5DiskStore.open(cacheDir)
    OldScapeServer(config.revision, config.port, Js5TransferCache.create(diskStore)).run()
}