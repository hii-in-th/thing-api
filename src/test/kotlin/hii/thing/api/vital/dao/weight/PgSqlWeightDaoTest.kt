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

package hii.thing.api.vital.dao.weight

import hii.thing.api.PgSqlTestRule
import hii.thing.api.`should be equal to`
import hii.thing.api.device.Device
import hii.thing.api.device.dao.PgSqlDeviceDao
import hii.thing.api.ignore
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.CreateSessionDetail.InputType.CARD
import hii.thing.api.sessions.CreateSessionDetail.Sex.MALE
import hii.thing.api.sessions.dao.recordsession.PgSqlRecordSessionDao
import hii.thing.api.vital.Weight
import org.amshove.kluent.`should be equal to`
import org.jetbrains.exposed.sql.Table
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PgSqlWeightDaoTest {
    @JvmField
    @Rule
    val pgsql = PgSqlTestRule(Table(SqlWeight.tableName))
    lateinit var weightDao: WeightDao

    val sessionId = "max-199-991-888"
    val sessionId2 = "max-199-991-889"

    @Before
    fun setUp() {
        weightDao = PgSqlWeightDao(pgsql.connection)
        ignore {
            val deviceId = "max990"
            val device = PgSqlDeviceDao(pgsql.connection)
            val session = PgSqlRecordSessionDao(pgsql.connection)
            val detail = CreateSessionDetail(deviceId, "1234122345634", CARD, "1970-10-13", "thanachai", MALE)

            ignore { device.create(Device("nstda", deviceId, "sss")) }
            ignore { session.register(sessionId, detail) }
            ignore { session.register(sessionId2, detail) }
        }
    }

    val weight = Weight(68.2F)

    @Test
    fun save() {
        val save = weightDao.save(sessionId, weight)

        save.weight `should be equal to` weight.weight
        save.time `should be equal to` weight.time
        save.sessionId!! `should be equal to` sessionId
    }

    @Test(expected = Exception::class)
    fun saveDuplicateSession() {
        weightDao.save(sessionId, weight)
        weightDao.save(sessionId, Weight(45.9F))
    }

    @Test
    fun getBy() {
        weightDao.save(sessionId, weight)
        weightDao.save(sessionId2, Weight(50F))

        val get = weightDao.getBy(sessionId)

        get.weight `should be equal to` weight.weight
        get.time `should be equal to` weight.time
        get.sessionId!! `should be equal to` sessionId
    }
}
