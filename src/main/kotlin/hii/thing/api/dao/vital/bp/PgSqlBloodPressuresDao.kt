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

package hii.thing.api.dao.vital.bp

import hii.thing.api.dao.Now
import hii.thing.api.vital.BloodPressures
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class PgSqlBloodPressuresDao(connection: () -> Connection) :
    BloodPressuresDao {
    init {
        Database.connect(connection)
    }

    override fun save(session: String, bp: BloodPressures): BloodPressures {
        var bpOut: BloodPressures? = null
        transaction {
            SchemaUtils.create(SqlBloodPressures)
            require(runCatching { getBy(session) }.isFailure)
            SqlBloodPressures.insert {
                it[dia] = bp.dia
                it[sys] = bp.sys
                it[pulse] = bp.pulse
                it[sessionId] = session
                it[time] = Now()
            }

            bpOut = getBy(session)
        }
        return bpOut!!
    }

    override fun getBy(session: String): BloodPressures {
        var bpOut: BloodPressures? = null
        transaction {
            SqlBloodPressures.select { (SqlBloodPressures.sessionId eq session) }
                .limit(1).map {
                    bpOut = SqlBloodPressures.getResult(it)
                }
        }
        return bpOut!!
    }
}
