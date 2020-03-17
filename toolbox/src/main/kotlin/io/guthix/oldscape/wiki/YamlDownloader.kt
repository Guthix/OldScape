package io.guthix.oldscape.wiki

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import mu.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger {  }

fun main(args: Array<String>) {
    YamlDownloader.main(args)
}

object YamlDownloader {
    @JvmStatic
    fun main(args: Array<String>) {
        val cacheDir = Path.of(javaClass.getResource("/cache").toURI())
        val yamlFactory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        val objectMapper = ObjectMapper(yamlFactory)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerKotlinModule()


        val objectFile = Path.of(javaClass.getResource("/").toURI()).resolve("Objects.yaml").toFile()
        val objectsWiki = objectWikiDownloader(cacheDir)
        objectMapper.writeValue(objectFile, objectsWiki)
        logger.info { "Done writing objects" }

        val npcFile = Path.of(javaClass.getResource("/").toURI()).resolve("Npcs.yaml").toFile()
        val npcWikiData = npcWikiDownloader(cacheDir)
        objectMapper.writeValue(npcFile, npcWikiData)
        logger.info { "Done writing npcs" }
    }
}