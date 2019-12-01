/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.wiki

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.url
import mu.KotlinLogging
import java.io.IOException
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberProperties

private val logger = KotlinLogging.logger {}

private const val wikiUrl = "https://oldschool.runescape.wiki"

/** Scrapes the wiki and retrieves the wiki text.*/
@KtorExperimentalAPI
suspend fun scrapeWikiText(wikiType: String, id: Int, name: String): String {
    HttpClient(CIO) { followRedirects = false }.use { client ->
        val urlName = name.replace(' ', '_').replace("<.*?>".toRegex(), "");
        val queryUrl = if(urlName.contains("%")) {
            "$wikiUrl/w/Special:Lookup?type=$wikiType&id=$id"
        } else {
            "$wikiUrl/w/Special:Lookup?type=$wikiType&id=$id&name=$urlName"
        }
        logger.info("REQUEST - QUERY - $queryUrl")
        val redirect = client.call(queryUrl).response.headers["location"]
            ?: throw IOException("Could not retrieve redirect for $queryUrl")
        redirect.let {
            val rawUrl = "${it.substringBefore("#")}?action=raw"
            logger.info("REQUEST - RAW - $rawUrl")
            return client.get(rawUrl)
        }
    }
}