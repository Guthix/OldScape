/*
 * Copyright 2018-2021 Guthix
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
package io.guthix.oldscape.server.db

import io.guthix.oldscape.server.ServerConfig
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLException
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger { }

object PostgresDb {
    var initialized = false

    fun initialize(config: ServerConfig.DB) {
        Database.connect(
            url = "jdbc:postgresql:${config.url}",
            driver = "org.postgresql.Driver",
            user = config.username,
            password = config.password
        )

        transaction {
            initialized = try {
                SchemaUtils.create(PlayerTable, PlayerPropertiesTable)
                true
            } catch(e: PSQLException) {
                logger.error { "Could not connect to Postgres db at: ${config.url} username: ${config.username}" }
                false
            }
        }

    }
}

object PlayerTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val username: Column<String> = varchar("name", 50)
    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "pk_player")
}

object PlayerPropertiesTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val playerId: Column<Int> = reference("playerid", PlayerTable.id)
    val name: Column<String> = text("name").uniqueIndex()
    val type: Column<String> = text("type")
    val property: Column<String> = jsonb("property")
    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "pk_player_property")
}

internal object KotlinClass {
    private val kotlinTypesMap = mapOf(
        Boolean::class.qualifiedName to Boolean::class,
        Double::class.qualifiedName to Double::class,
        Float::class.qualifiedName to Float::class,
        Long::class.qualifiedName to Long::class,
        Int::class.qualifiedName to Int::class,
        Short::class.qualifiedName to Short::class,
        Byte::class.qualifiedName to Byte::class,
        String::class.qualifiedName to String::class
    )

    fun fromName(name: String): KClass<out Any> = kotlinTypesMap.getOrElse(name) { Class.forName(name).kotlin }
}