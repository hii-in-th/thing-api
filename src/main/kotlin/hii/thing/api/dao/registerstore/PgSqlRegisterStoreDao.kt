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

package hii.thing.api.dao.registerstore

import hii.thing.api.sessions.CreateSessionDetail
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import java.sql.Connection

class PgSqlRegisterStoreDao(connection: () -> Connection) :
    RegisterStoreDao {

    init {
        Database.connect(connection)
    }

    override fun register(sessionId: String, sessionDetail: CreateSessionDetail): CreateSessionDetail {
        var reg: CreateSessionDetail? = null
        transaction {
            SchemaUtils.create(SqlRegisterDetail)
            require(runCatching { get(sessionId) }.isFailure)
            try {
                SqlRegisterDetail.insert {
                    it[SqlRegisterDetail.sessionId] = sessionId
                    it[deviceId] = sessionDetail.deviceId
                    it[citizenId] = sessionDetail.citizenId
                    it[citizenIdInput] = sessionDetail.citizenIdInput
                    it[birthDate] = sessionDetail.birthDate
                    it[time] = DateTime.now()
                }
            } catch (ex: ExposedSQLException) {
                require(false)
            }

            SqlRegisterDetail.select { SqlRegisterDetail.sessionId eq sessionId }.limit(1)
                .map { reg = mapResult(it) }
        }
        return reg!!
    }

    private fun mapResult(it: ResultRow): CreateSessionDetail {
        return CreateSessionDetail(
            it[SqlRegisterDetail.deviceId],
            it[SqlRegisterDetail.citizenId],
            it[SqlRegisterDetail.citizenIdInput],
            it[SqlRegisterDetail.birthDate]
        )
    }

    override fun update(sessionId: String, sessionDetail: CreateSessionDetail): CreateSessionDetail {
        var reg: CreateSessionDetail? = null
        transaction {
            try {
                SqlRegisterDetail.update({ SqlRegisterDetail.sessionId eq sessionId }) {
                    it[deviceId] = sessionDetail.deviceId
                    it[citizenId] = sessionDetail.citizenId
                    it[citizenIdInput] = sessionDetail.citizenIdInput
                    it[birthDate] = sessionDetail.birthDate
                }
            } catch (ex: ExposedSQLException) {
                require(false)
            }
            SqlRegisterDetail.select { SqlRegisterDetail.sessionId eq sessionId }.limit(1)
                .map { reg = mapResult(it) }
        }
        return reg!!
    }

    override fun getBy(citizenId: String): Map<String, CreateSessionDetail> {
        val resultAll = hashMapOf<String, CreateSessionDetail>()
        transaction {
            SqlRegisterDetail.select { SqlRegisterDetail.citizenId eq citizenId }
                .map {
                    resultAll[it[SqlRegisterDetail.sessionId]] = mapResult(it)
                }
        }
        return resultAll.takeIf { it.isNotEmpty() }!!
    }

    override fun get(sessionId: String): CreateSessionDetail {
        var reg: CreateSessionDetail? = null
        transaction {
            SqlRegisterDetail.select { SqlRegisterDetail.sessionId eq sessionId }.limit(1)
                .map { reg = mapResult(it) }
        }
        return reg!!
    }
}
