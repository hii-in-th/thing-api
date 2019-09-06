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

package hii.thing.api.sessions.dao.recordsession

import hii.thing.api.Now
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sqlSequentCreateDao
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.sql.Connection

class PgSqlRecordSessionDao(connection: () -> Connection) :
    RecordSessionDao {

    init {
        Database.connect(connection)
        transaction { SchemaUtils.create(SqlSessionDetail) }
    }

    override fun register(sessionId: String, sessionDetail: CreateSessionDetail): CreateSessionDetail {
        var reg: CreateSessionDetail? = null
        transaction {
            require(runCatching { get(sessionId) }.isFailure)
            try {
                SqlSessionDetail.insert {
                    it[SqlSessionDetail.sessionId] = sessionId
                    it[deviceId] = sessionDetail.deviceId
                    it[citizenId] = sessionDetail.citizenId
                    it[citizenIdInput] = sessionDetail.citizenIdInput?.toString()
                    it[birthDate] = sessionDetail.birthDate?.toJavaTime()?.toSqlTime()
                    it[name] = sessionDetail.name
                    it[time] = Now()
                    it[sex] = sessionDetail.sex?.toString()
                }
            } catch (ex: ExposedSQLException) {
                ex.message?.let {
                    if (it.contains("violates foreign key constraint"))
                        sqlSequentCreateDao.invoke()
                }
                throw ex
                // require(false)
            }

            SqlSessionDetail.select { SqlSessionDetail.sessionId eq sessionId }.limit(1)
                .map { reg = mapResult(it) }
        }
        return reg!!
    }

    private fun mapResult(it: ResultRow): CreateSessionDetail {
        return CreateSessionDetail(
            it[SqlSessionDetail.deviceId],
            it[SqlSessionDetail.citizenId],
            try {
                CreateSessionDetail.InputType.valueOf(it[SqlSessionDetail.citizenIdInput]!!.toUpperCase())
            } catch (ignore: Exception) {
                null
            },
            it[SqlSessionDetail.birthDate]?.toStringDate(),
            it[SqlSessionDetail.name],
            try {
                CreateSessionDetail.Sex.valueOf(it[SqlSessionDetail.sex]!!.toUpperCase())
            } catch (ignore: Exception) {
                null
            }
        )
    }

    override fun update(sessionId: String, sessionDetail: CreateSessionDetail): CreateSessionDetail {
        var reg: CreateSessionDetail? = null
        transaction {
            try {
                SqlSessionDetail.update({ SqlSessionDetail.sessionId eq sessionId }) {
                    it[deviceId] = sessionDetail.deviceId
                    it[citizenId] = sessionDetail.citizenId
                    it[citizenIdInput] = sessionDetail.citizenIdInput?.toString()
                    it[name] = sessionDetail.name
                    it[birthDate] = sessionDetail.birthDate?.toJavaTime()?.toSqlTime()
                    it[sex] = sessionDetail.sex?.toString()
                }
            } catch (ex: ExposedSQLException) {
                require(false)
            }
            SqlSessionDetail.select { SqlSessionDetail.sessionId eq sessionId }.limit(1)
                .map { reg = mapResult(it) }
        }
        return reg!!
    }

    override fun getBy(citizenId: String): Map<String, CreateSessionDetail> {
        val resultAll = hashMapOf<String, CreateSessionDetail>()
        transaction {
            SqlSessionDetail.select { SqlSessionDetail.citizenId eq citizenId }
                .map {
                    resultAll[it[SqlSessionDetail.sessionId]] = mapResult(it)
                }
        }
        return resultAll.takeIf { it.isNotEmpty() }!!
    }

    override fun get(sessionId: String): CreateSessionDetail {
        var reg: CreateSessionDetail? = null
        transaction {
            SqlSessionDetail.select { SqlSessionDetail.sessionId eq sessionId }.limit(1)
                .map { reg = mapResult(it) }
        }
        return reg!!
    }
}
