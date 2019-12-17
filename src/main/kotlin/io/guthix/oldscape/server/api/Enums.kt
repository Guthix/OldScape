package io.guthix.oldscape.server.api

import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.server.Cache
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

object Enums {
    private lateinit var configs: Map<Int, EnumConfig>

    operator fun get(index: Int) = configs[index]

    fun load() {
        configs = EnumConfig.load(Cache.getGroup(ConfigArchive.id, EnumConfig.id))
        logger.info { "Loaded ${configs.size} enums" }
    }
}