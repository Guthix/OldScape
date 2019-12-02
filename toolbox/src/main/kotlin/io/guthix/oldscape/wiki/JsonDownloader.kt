package io.guthix.oldscape.wiki

import kotlinx.serialization.json.JsonConfiguration

abstract class JsonDownloader {
    val config = JsonConfiguration.Stable.copy(encodeDefaults = false, prettyPrint = true)
}