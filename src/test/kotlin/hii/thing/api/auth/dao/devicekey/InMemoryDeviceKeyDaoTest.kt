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

import hii.thing.api.auth.DeviceKeyDetail
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Test

class InMemoryDeviceKeyDaoTest {

    val deviceKeyDao: DeviceKeyDao = InMemoryDeviceKeyDao()

    val device = DeviceKeyDetail(
        "hii/007",
        "abcde",
        listOf("kios"),
        listOf("/vital", "/height", "/bmi")
    )

    @Test
    fun registerDevice() {
        deviceKeyDao.registerDevice(device)
    }

    @Test
    fun registerAndGet() {
        deviceKeyDao.registerDevice(device)
        val getDevice = deviceKeyDao.getDeviceBy(device.deviceKey)

        getDevice.deviceID `should be equal to` device.deviceID
        getDevice.deviceName `should be equal to` device.deviceName
        getDevice.deviceKey `should be equal to` device.deviceKey
        getDevice.roles `should equal` device.roles
        getDevice.scope `should equal` device.scope
    }
}
