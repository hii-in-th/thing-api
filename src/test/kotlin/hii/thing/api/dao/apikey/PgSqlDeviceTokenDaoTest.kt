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

package hii.thing.api.dao.apikey

import hii.thing.api.PgSqlTestRule
import hii.thing.api.auth.DeviceToken
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.jetbrains.exposed.sql.Table
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PgSqlDeviceTokenDaoTest {
    @JvmField
    @Rule
    val pgsql = PgSqlTestRule(Table("keystore"))
    lateinit var deviceTokenDao: DeviceTokenDao

    @Before
    fun setUp() {
        deviceTokenDao = PgSqlDeviceTokenDao(pgsql.connection)
    }

    val device = DeviceToken(
        "hii/007",
        "abcde",
        listOf("kios"),
        listOf("/vital", "/height", "/bmi")
    )

    @Test
    fun registerDevice() {
        deviceTokenDao.registerDevice(device)
    }

    @Test
    fun registerAndGet() {
        deviceTokenDao.registerDevice(device)
        val getDevice = deviceTokenDao.getDeviceBy(device.baseToken)

        getDevice.deviceID `should be equal to` device.deviceID
        getDevice.deviceName `should be equal to` device.deviceName
        getDevice.baseToken `should be equal to` device.baseToken
        getDevice.roles `should equal` device.roles
        getDevice.scope `should equal` device.scope
    }
}
