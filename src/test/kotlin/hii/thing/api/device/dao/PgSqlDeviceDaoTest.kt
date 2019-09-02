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

package hii.thing.api.device.dao

import hii.thing.api.PgSqlTestRule
import hii.thing.api.device.Device
import org.amshove.kluent.`should be equal to`
import org.jetbrains.exposed.sql.Table
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PgSqlDeviceDaoTest {
    @JvmField
    @Rule
    val pgsql = PgSqlTestRule(Table(SqlDevice.tableName))
    lateinit var dao: DeviceDao

    private val deviceId = "12345-3453-435"
    private val location = "คลองหลวง 999"
    private val type = "hii"

    private val deviceId2 = "213213-21321-34-13"
    private val location2 = "สาธร 888"
    private val type2 = "888"

    private val device = Device(location, deviceId, type)
    private val device2 = Device(location2, deviceId2, type2)

    @Test
    fun create() {
        dao.create(device)
    }

    @Test
    fun createMulti() {
        dao.create(device)
        dao.create(device2)
    }

    @Test
    fun createAndGet() {
        dao.create(device)

        val create = dao.get(deviceId)

        create.location `should be equal to` location
        create.deviceId!! `should be equal to` deviceId
        create.type!! `should be equal to` type
    }

    @Test
    fun createMultiAndGetMulti() {
        dao.create(device)
        dao.create(device2)

        val create = dao.get(deviceId)
        val create2 = dao.get(deviceId2)

        create.location `should be equal to` location
        create.deviceId!! `should be equal to` deviceId
        create.type!! `should be equal to` type

        create2.location `should be equal to` location2
        create2.deviceId!! `should be equal to` deviceId2
        create2.type!! `should be equal to` type2
    }

    @Test
    fun createAndCheckValue() {
        val create = dao.create(device)

        create.location `should be equal to` location
        create.deviceId!! `should be equal to` deviceId
        create.type!! `should be equal to` type
    }

    @Test
    fun createMultiAndCheckMulti() {
        val create = dao.create(device)
        val create2 = dao.create(device2)

        create.location `should be equal to` location
        create.deviceId!! `should be equal to` deviceId
        create.type!! `should be equal to` type

        create2.location `should be equal to` location2
        create2.deviceId!! `should be equal to` deviceId2
        create2.type!! `should be equal to` type2
    }

    @Test(expected = IllegalArgumentException::class)
    fun createDuplicate() {
        dao.create(device)
        dao.create(device)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getEmpty() {
        dao.get(deviceId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getEmpty2() {
        dao.create(device2)
        dao.get(deviceId)
    }

    @Test
    fun createAndUpdate() {
        dao.create(device)
        dao.update(deviceId, Device("สามเสน", deviceId, "nectec"))
    }

    @Test
    fun createAndUpdateAndCheck() {
        dao.create(device)
        val location1 = "สามเสน"
        val type1 = "nectec"

        val update = dao.update(deviceId, Device(location1, deviceId, type1))

        update.location `should be equal to` location1
        update.deviceId!! `should be equal to` deviceId
        update.type!! `should be equal to` type1
    }

    @Test(expected = IllegalArgumentException::class)
    fun updateEmpty() {
        dao.update(deviceId, Device("ทองหล่อ", deviceId, "nectec"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun updateEmpty2() {
        dao.create(device2)
        dao.update(deviceId, Device("ทองหล่อ", deviceId, "nectec"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun updateWrongDeviceId() {
        dao.create(device2)
        dao.update(deviceId, Device("ทองหล่อ", deviceId2, "nectec"))
    }

    @Before
    fun setUp() {
        dao = PgSqlDeviceDao(pgsql.connection)
    }
}
