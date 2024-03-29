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

package hii.thing.api.vital.dao.bp

import hii.thing.api.PgSqlTestRule
import hii.thing.api.`should be equal to`
import hii.thing.api.device.Device
import hii.thing.api.device.dao.PgSqlDeviceDao
import hii.thing.api.ignore
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.CreateSessionDetail.InputType.CARD
import hii.thing.api.sessions.CreateSessionDetail.Sex.MALE
import hii.thing.api.sessions.dao.recordsession.PgSqlRecordSessionDao
import hii.thing.api.vital.BloodPressures
import org.amshove.kluent.`should be equal to`
import org.jetbrains.exposed.sql.Table
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PgSqlBloodPressuresDaoTest {
    @JvmField
    @Rule
    val pgsql = PgSqlTestRule(Table(SqlBloodPressures.tableName))

    lateinit var pbDao: PgSqlBloodPressuresDao
    var sessionId: String = "13432-sdfdsf-dsf-344435"

    @Before
    fun setUp() {
        pbDao = PgSqlBloodPressuresDao(pgsql.connection)
        ignore {
            val deviceId = "max990"
            val device = PgSqlDeviceDao(pgsql.connection)
            val session = PgSqlRecordSessionDao(pgsql.connection)
            val detail = CreateSessionDetail(deviceId, "1234122345634", CARD, "1970-10-13", "thanachai", MALE)

            ignore { device.create(Device("sss", "nstda", "sss", deviceId)) }
            ignore { session.register(sessionId, detail) }
        }
    }

    val bp = BloodPressures(
        123.0F,
        140.6F,
        60.0F
    )
    val pb2 = BloodPressures(
        110.0F,
        60.6F,
        58F
    )

    @Test
    fun save() {
        val save = pbDao.save(sessionId, bp)

        save.sessionId!! `should be equal to` sessionId
        save.dia `should be equal to` bp.dia
        save.sys `should be equal to` bp.sys
        save.time `should be equal to` bp.time
        save.pulse `should be equal to` bp.pulse
    }

    @Test(expected = Exception::class)
    fun saveDuplicate() {
        pbDao.save(sessionId, bp)
        pbDao.save(sessionId, pb2)
    }

    @Test
    fun getBy() {
        pbDao.save(sessionId, bp)

        val get = pbDao.getBy(sessionId)

        get.sessionId!! `should be equal to` sessionId
        get.dia `should be equal to` bp.dia
        get.sys `should be equal to` bp.sys
        get.time `should be equal to` bp.time
        get.pulse `should be equal to` bp.pulse
    }

    @Test(expected = Exception::class)
    fun getEmpty() {
        pbDao.getBy(sessionId)
    }
}
