/*
 * Copyright (c) 2019 NSTDA
 *   National Science and Technology Development Agency, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hii.thing.api.dao.lastresult

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.sql.Connection

private const val delimiter = "|"

class PgSqlLastResultDao(connection: () -> Connection) : LastResultDao {
    init {
        Database.connect(connection)
    }

    override fun set(citizenId: String, result: Map<String, String>): Map<String, String> {
        val strValue: String = createStrValue(result)
        try {
            transaction {
                SchemaUtils.create(SqlLastResult)
                SqlLastResult.insert {
                    it[SqlLastResult.citizenId] = citizenId
                    it[value] = strValue
                }
            }
        } catch (update: ExposedSQLException) {
            transaction {
                SqlLastResult.update({ SqlLastResult.citizenId eq citizenId }) {
                    it[value] = strValue
                }
            }
        }
        return get(citizenId)
    }

    private fun createStrValue(result: Map<String, String>): String {
        var strValue = ""
        result.forEach { (key, value) -> strValue += "$key$delimiter$value$delimiter$delimiter" }
        return strValue
    }

    override fun append(citizenId: String, result: Map<String, String>): Map<String, String> {
        transaction {
            SchemaUtils.create(SqlLastResult)
            val appendResult = hashMapOf<String, String>()
            val whereQuery = SqlLastResult.citizenId eq citizenId
            SqlLastResult.select { whereQuery }.limit(1)
                .map {
                    it[SqlLastResult.value].split("$delimiter$delimiter").forEach { listResult ->
                        val keyMap = listResult.split(delimiter)
                        appendResult[keyMap.first()] = keyMap.last()
                    }
                }

            appendResult.putAll(result)
            appendResult.remove("")

            SqlLastResult.update({ whereQuery }) {
                it[value] = createStrValue(appendResult)
            }
        }
        return try {
            get(citizenId)
        } catch (ex: KotlinNullPointerException) {
            set(citizenId, result)
        }
    }

    override fun get(citizenId: String): Map<String, String> {
        var output: Map<String, String>? = null
        transaction {
            SchemaUtils.create(SqlLastResult)
            SqlLastResult.select { SqlLastResult.citizenId eq citizenId }.limit(1)
                .map {
                    output = mapToResult(it)
                }
        }
        return output.takeIf { !it.isNullOrEmpty() }!!
    }

    private fun mapToResult(it: ResultRow): HashMap<String, String> {
        val output = hashMapOf<String, String>()
        it[SqlLastResult.value].split("$delimiter$delimiter").forEach { listResult ->
            val keyMap = listResult.split(delimiter)
            output[keyMap.first()] = keyMap.last()
        }
        return output
    }

    override fun remove(citizenId: String) {
        transaction {
            SqlLastResult.deleteWhere { SqlLastResult.citizenId eq citizenId }
        }
    }
}
