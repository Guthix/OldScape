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
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse

private const val wikiUrl = "https://oldschool.runescape.wiki"

/** Scrapes the wiki and retrieves the wiki text.*/
suspend fun HttpClient.scrapeWikiText(wikiType: String, id: Int, name: String): String {
    val urlName = name.replace(' ', '_').replace("<.*?>".toRegex(), "");
    val queryUrl = if(urlName.contains("%")) {
        "$wikiUrl/w/Special:Lookup?type=$wikiType&id=$id"
    } else {
        "$wikiUrl/w/Special:Lookup?type=$wikiType&id=$id&name=$urlName"
    }
    val redirect = request<HttpResponse>(queryUrl).call.response.headers["location"]
        ?: throw PageNotFoundException("Could not retrieve redirect for $queryUrl")
    if(redirect.contains("search")) throw PageNotFoundException("Could not retrieve redirect for $queryUrl")
    val rawUrl = "${redirect.substringBefore("#")}?action=raw"
    return get(rawUrl)
}

class PageNotFoundException(message: String) : Exception(message)