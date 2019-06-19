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

package hii.thing.api.dao.vital.height

import hii.thing.api.vital.Height
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.sql.Connection

class PgSqlHeightsDao(connection: () -> Connection) : HeightsDao {
    init {
        Database.connect(connection)
    }

    override fun save(session: String, height: Height): Height {
        var heightOut: Height? = null
        transaction {
            SchemaUtils.create(SqlHeight)
            require(runCatching { getBy(session) }.isFailure)
            SqlHeight.insert {
                it[sessionId] = session
                it[SqlHeight.height] = height.height
                it[time] = DateTime.now()
            }

            heightOut = getBy(session)
        }
        return heightOut!!
    }

    override fun getBy(session: String): Height {
        var heightOut: Height? = null
        transaction {
            SqlHeight.select { SqlHeight.sessionId eq session }.limit(1)
                .map { heightOut = SqlHeight.getResult(it) }
        }
        return heightOut!!
    }
}
