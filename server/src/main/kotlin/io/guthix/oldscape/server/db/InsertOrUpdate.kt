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
package io.guthix.oldscape.server.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

fun <T : Table> T.upsert(
    conflictColumn: Column<*>? = null,
    body: T.(UpsertStatement<Number>) -> Unit
): UpsertStatement<Number> = UpsertStatement<Number>(this, conflictColumn).apply {
    body(this)
    execute(TransactionManager.current())
}

class UpsertStatement<Key : Any>(
    table: Table,
    conflictColumn: Column<*>? = null
) : InsertStatement<Key>(table, false) {
    private val indexName: String
    private val indexColumns: List<Column<*>>
    private val index: Boolean

    init {
        when {
            conflictColumn != null -> {
                index = false
                indexName = conflictColumn.name
                indexColumns = listOf(conflictColumn)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun prepareSQL(transaction: Transaction): String = buildString {
        append(super.prepareSQL(transaction))

        val dialect = transaction.db.vendor
        if (dialect == "postgresql") {
            if (index) {
                append(" ON CONFLICT ON CONSTRAINT ")
                append(indexName)
            } else {
                append(" ON CONFLICT(")
                append(indexName)
                append(")")
            }
            append(" DO UPDATE SET ")
            values.keys.filter { it !in indexColumns }
                .joinTo(this) { "${transaction.identity(it)}=EXCLUDED.${transaction.identity(it)}" }
        } else {
            append(" ON DUPLICATE KEY UPDATE ")
            values.keys.filter { it !in indexColumns }
                .joinTo(this) { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }

        }
    }

}
//fun Table.indexR(customIndexName: String? = null, isUnique: Boolean = false, vararg columns: Column<*>): Index {
//    val index = Index(columns.toList(), isUnique, customIndexName)
//    indices.add(index)
//    return index
//}
//
//fun Table.uniqueIndexR(customIndexName: String? = null, vararg columns: Column<*>): Index =
//    indexR(customIndexName, true, *columns)