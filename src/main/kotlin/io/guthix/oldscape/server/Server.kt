package io.guthix.oldscape.server

import com.charleskorn.kaml.Yaml
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val configFile = Path.of(ServerConfig::class.java.getResource("/Config.yaml").toURI())
    val config = Yaml.default.parse(ServerConfig.serializer(), Files.readString(configFile))
}