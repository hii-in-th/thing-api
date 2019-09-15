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

package hii.thing.api.auth.dao.devicekey

import hii.thing.api.InMemoryTestRule
import hii.thing.api.auth.DeviceKeyDetail
import hii.thing.api.security.keypair.KeyPairManage
import hii.thing.api.security.keypair.dao.DemoRSAKeyPairDao
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain some`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should not equal`
import org.junit.Before
import org.junit.Test

class JwtDeviceKeyDaoTest {
    val rule = InMemoryTestRule()

    val deviceKeyDao: DeviceKeyDao = JwtDeviceKeyDao()

    val device = DeviceKeyDetail(
        "hii/007",
        "",
        listOf("kios"),
        listOf("/vital", "/height", "/bmi")
    )

    @Before
    fun setUp() {
        KeyPairManage.setUp(DemoRSAKeyPairDao())
    }

    @Test
    fun registerDevice() {
        deviceKeyDao.registerDevice(device)
    }

    @Test
    fun registerAndGet() {
        val register = deviceKeyDao.registerDevice(device)
        val getDevice = deviceKeyDao.getDeviceBy(register.deviceKey)

        getDevice.deviceID `should equal` device.deviceID
        getDevice.deviceName `should be equal to` device.deviceName
        getDevice.deviceKey `should not equal` device.deviceKey
        getDevice.roles `should equal` device.roles
        getDevice.scope `should contain some` device.scope
    }
}
