/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.wiki

import mu.KotlinLogging
import java.time.LocalDate
import java.time.Month
import kotlin.reflect.full.createInstance

private val logger = KotlinLogging.logger {}

public abstract class WikiDefinition<P : WikiDefinition<P>> {
    public open var ids: List<Int>? = null
    public open var name: String? = null

    @Suppress("UNCHECKED_CAST")
    public open fun parse(page: String, version: Int? = null): P {
        page.reader().readLines().forEach { pageLine ->
            try {
                if (pageLine.startsWith("|")) {
                    parseKeyValueLine(pageLine, version)
                }
            } catch (e: Exception) {
                logger.warn("Could not parse line: $pageLine")
            }
        }
        return this as P
    }

    public abstract fun parseKeyValueLine(line: String, version: Int?)

    protected fun String.checkWikiKey(matcher: String, version: Int?): Boolean {
        val lineCheck = substringBefore("=").replace(" ", "").substring(1)
        val matchCheck = matcher.replace(" ", "")
        return if (version == null) {
            matchCheck.equals(lineCheck, true)
        } else {
            matchCheck.equals(lineCheck, true) or "$matchCheck$version".equals(lineCheck, true)
        }
    }

    protected fun String.getWikiString(): String? {
        val str = substringAfter("=").removePrefix(" ")
        return if (str.equals("", ignoreCase = true) || str.equals("N/A", ignoreCase = true)) null else str
    }

    protected fun String.getWikiStrings(): List<String>? = getWikiString()
        ?.split(",")?.map { it.replace(" ", "") }

    protected fun String.getWikiBool(): Boolean? = getWikiString()
        ?.replace(" ", "")
        ?.equals("Yes", ignoreCase = true)

    protected fun String.getWikiInt(): Int? = getWikiString()
        ?.replace(",", "")
        ?.replace("\\D", "")
        ?.replace(" ", "")
        ?.toInt()

    protected fun String.getWikiFloat(): Float? = getWikiString()
        ?.toFloat()

    protected fun String.getWikiNoString(): String? {
        if (contains("No", ignoreCase = true)) return null
        return getWikiString()
    }

    protected fun String.getWikiNoInt(): Int? = getWikiNoString()
        ?.toInt()

    protected fun String.getWikiNoDouble(): Double? = getWikiNoString()
        ?.toDouble()

    protected fun String.getWikiDate(): LocalDate {
        val splits = split(" ").subList(2, 5)
        return LocalDate.of(
            splits[2].replace("[", "").replace("]", "").toInt(),
            Month.valueOf(splits[1].replace("]", "").toUpperCase()),
            splits[0].replace("[", "").toInt()
        )
    }

    protected fun String.getIds(): List<Int>? = if (getWikiString().equals("Removed", ignoreCase = true)) {
        null
    } else {
        getWikiStrings()?.map(String::toInt)
    }
}

public inline fun <reified P : WikiDefinition<P>> parseWikiString(wikiString: String): List<P> {
    val definitions = mutableListOf<P>()
    if (wikiString.contains("|id1 = ")) {
        var version = 1
        do {
            val def = P::class.createInstance().parse(wikiString, version)
            definitions.add(def)
            version++
        } while (wikiString.contains("|id$version = "))
    } else {
        val def = P::class.createInstance().parse(wikiString, null)
        definitions.add(def)
    }
    return definitions
}

public abstract class WikiConfigCompanion {
    public abstract val queryString: String
}