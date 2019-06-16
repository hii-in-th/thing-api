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

package hii.thing.api.dao.pgsql

import hii.thing.api.auth.Device
import hii.thing.api.dao.ApiKeyDao
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres

class PgSqlApiKeyDaoTest {
    val pgsql = EmbeddedPostgres()
    lateinit var apiKeyDao: ApiKeyDao

    @Before
    fun setUp() {
        val url = pgsql.start()
        apiKeyDao = PgSqlApiKeyDao(url, "postgres", "postgres")
    }

    val device = Device(
        "hii/007",
        "abcde",
        "api.ffc.in.th",
        listOf("kios"),
        listOf("/vital", "/height", "/bmi")
    )

    @Test
    fun registerDevice() {
        apiKeyDao.registerDevice(device)
    }

    @Test
    fun registerAndGet() {
        apiKeyDao.registerDevice(device)
        val getDevice = apiKeyDao.getDeviceBy(device.baseToken)

        getDevice.deviceID `should be equal to` device.deviceID
        getDevice.deviceName `should be equal to` device.deviceName
        getDevice.audience `should be equal to` device.audience
        getDevice.baseToken `should be equal to` device.baseToken
        getDevice.roles `should equal` device.roles
        getDevice.scope `should equal` device.scope
    }

    @After
    fun tearDown() {
        pgsql.stop()
    }
}
