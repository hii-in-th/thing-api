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

package hii.thing.api.vital.dao.height

import hii.thing.api.PgSqlTestRule
import hii.thing.api.`should be equal to`
import hii.thing.api.device.Device
import hii.thing.api.device.dao.PgSqlDeviceDao
import hii.thing.api.getLogger
import hii.thing.api.ignore
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.CreateSessionDetail.InputType.CARD
import hii.thing.api.sessions.CreateSessionDetail.Sex.MALE
import hii.thing.api.sessions.dao.recordsession.PgSqlRecordSessionDao
import hii.thing.api.vital.Height
import org.amshove.kluent.`should be equal to`
import org.jetbrains.exposed.sql.Table
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PgSqlHeightsDaoTest {
    @JvmField
    @Rule
    val pgsql = PgSqlTestRule(Table(SqlHeight.tableName))
    lateinit var heightsDao: HeightsDao

    val sessionId = "max-999-888-111"

    @Before
    fun setUp() {
        heightsDao = PgSqlHeightsDao(pgsql.connection)
        ignore {
            val deviceId = "max990"
            val device = PgSqlDeviceDao(pgsql.connection)
            val session = PgSqlRecordSessionDao(pgsql.connection)
            val detail = CreateSessionDetail(deviceId, "1234122345634", CARD, "1970-10-13", "thanachai", MALE)
            ignore { device.create(Device("sss", "nstda", "sss", deviceId)) }
            ignore { session.register(sessionId, detail) }
        }
    }

    val height = Height(170.1F)
    val height2 = Height(169.0F)

    @Test
    fun save() {
        val save = heightsDao.save(sessionId, height)

        save.sessionId!! `should be equal to` sessionId
        save.time `should be equal to` height.time
        save.height `should be equal to` height.height
    }

    @Test(expected = Exception::class)
    fun saveDuplicate() {
        val logger = getLogger()
        logger.info { "Save 1" }
        heightsDao.save(sessionId, height)
        logger.info { "Save 2" }
        val result = heightsDao.save(sessionId, height2)
        result.height `should be equal to` height2.height
    }

    @Test
    fun getBy() {
        heightsDao.save(sessionId, height)
        val get = heightsDao.getBy(sessionId)

        get.sessionId!! `should be equal to` sessionId
        get.time `should be equal to` height.time
        get.height `should be equal to` height.height
    }

    @Test(expected = Exception::class)
    fun getEmpty() {
        heightsDao.getBy(sessionId)
    }
}
