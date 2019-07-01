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

import hii.thing.api.auth.Device
import hii.thing.api.dao.keyspair.InMemoryRSAKeyPairDao
import hii.thing.api.security.keypair.KeyPairManage
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should not equal`
import org.junit.Before
import org.junit.Test

class JwtApiKeyDaoTest {

    val apiKeyDao: ApiKeyDao = JwtApiKeyDao()

    val device = Device(
        "hii/007",
        "",
        listOf("kios"),
        listOf("/vital", "/height", "/bmi")
    )

    @Before
    fun setUp() {
        KeyPairManage.setUp(InMemoryRSAKeyPairDao())
    }

    @Test
    fun registerDevice() {
        apiKeyDao.registerDevice(device)
    }

    @Test
    fun registerAndGet() {
        val register = apiKeyDao.registerDevice(device)
        val getDevice = apiKeyDao.getDeviceBy(register.baseToken)

        getDevice.deviceID `should not equal` device.deviceID
        getDevice.deviceName `should be equal to` device.deviceName
        getDevice.baseToken `should not equal` device.baseToken
        getDevice.roles `should equal` device.roles
        getDevice.scope `should equal` device.scope
    }
}
