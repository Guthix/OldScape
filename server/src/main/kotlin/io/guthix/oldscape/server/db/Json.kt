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

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

class JsonbColumnType : ColumnType() {
    override fun sqlType(): String = JSONB

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        super.setParameter(stmt, index, value.let {
            PGobject().apply {
                type = sqlType()
                this.value = value as String
            }
        })
    }

    override fun valueFromDB(value: Any): Any = if (value is PGobject) "$value" else value

    override fun valueToString(value: Any?): String = when (value) {
        is Iterable<*> -> nonNullValueToString(value)
        else -> super.valueToString(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun notNullValueToDB(value: Any): String = value as String

    companion object {
        const val JSONB: String = "JSONB"
        const val TEXT: String = "TEXT"
    }
}

fun Table.jsonb(name: String): Column<String> = registerColumn(name, JsonbColumnType())

class JsonValue<T>(
    val expr: Expression<*>,
    override val columnType: ColumnType,
    val jsonPath: List<String>
) : Function<T>(columnType) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        val castJson = columnType.sqlType() != JsonbColumnType.JSONB
        if (castJson) append("(")
        append(expr)
        append(" #>")
        if (castJson) append(">")
        append(" '{${jsonPath.joinToString(transform = ::escapeFieldName)}}'")
        if (castJson) append(")::${columnType.sqlType()}")
    }

    companion object {
        private fun escapeFieldName(value: String) = value.map {
            fieldNameCharactersToEscape[it] ?: it
        }.joinToString("").let { "\"$it\"" }

        private val fieldNameCharactersToEscape = mapOf(
            // '\"' to "\'\'", // no need to escape single quote as we put string in double quotes
            '\"' to "\\\"",
            '\r' to "\\r",
            '\n' to "\\n"
        )
    }
}

inline fun <reified T> Column<*>.json(vararg jsonPath: String): JsonValue<T> {
    val columnType = when (T::class) {
        Boolean::class -> BooleanColumnType()
        Int::class -> IntegerColumnType()
        Float::class -> FloatColumnType()
        String::class -> TextColumnType()
        else -> JsonbColumnType()
    }
    return JsonValue(this, columnType, jsonPath.toList())
}


class JsonContainsOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "??")

infix fun <T> JsonValue<Any>.contains(t: T): JsonContainsOp =
    JsonContainsOp(this, SqlExpressionBuilder.run { wrap(t) })

infix fun <T> JsonValue<Any>.contains(other: Expression<T>): JsonContainsOp =
    JsonContainsOp(this, other)