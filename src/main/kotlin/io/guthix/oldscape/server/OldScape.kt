package io.guthix.oldscape.server

import com.charleskorn.kaml.Yaml
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.server.net.OldScapeServer
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val configFile = Path.of(ServerConfig::class.java.getResource("/Config.yaml").toURI())
    val config = Yaml.default.parse(ServerConfig.serializer(), Files.readString(configFile))
    OldScapeServer(config.revision, config.port, 21, config.rsa.privateKey, config.rsa.modulus).run()
}