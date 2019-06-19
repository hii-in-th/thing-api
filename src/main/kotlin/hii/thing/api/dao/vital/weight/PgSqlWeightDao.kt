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

package hii.thing.api.dao.vital.weight

import hii.thing.api.vital.Weight
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.sql.Connection

class PgSqlWeightDao(connection: () -> Connection) : WeightDao {
    init {
        Database.connect(connection)
    }

    override fun save(session: String, weight: Weight): Weight {
        var weightOut: Weight? = null
        transaction {
            SchemaUtils.create(SqlWeight)
            require(runCatching { getBy(session) }.isFailure)

            SqlWeight.insert {
                it[sessionId] = session
                it[SqlWeight.weight] = weight.weight
                it[time] = DateTime.now()
            }
            weightOut = getBy(session)
        }
        return weightOut!!
    }

    override fun getBy(session: String): Weight {
        var weightOut: Weight? = null
        transaction {
            SqlWeight.select { SqlWeight.sessionId eq session }.limit(1).map { weightOut = SqlWeight.getResult(it) }
        }
        return weightOut!!
    }
}
