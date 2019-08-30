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
import hii.thing.api.auth.NotFoundToken

class InMemoryDeviceKeyDao : DeviceKeyDao {
    override fun getDeviceBy(deviceKey: String): DeviceKeyDetail {
        return storage[deviceKey] ?: throw NotFoundToken("ไม่พบ Api key")
    }

    override fun registerDevice(deviceKeyDetail: DeviceKeyDetail): DeviceKeyDetail {
        storage[deviceKeyDetail.deviceKey] = deviceKeyDetail
        return deviceKeyDetail
    }

    companion object {
        private val storage = linkedMapOf<String, DeviceKeyDetail>().apply {
            put("abcde", DeviceKeyDetail("hii/07", "abcde", listOf("kiosk"), listOf("/vital", "/height", "/bmi")))
            put("ab001", DeviceKeyDetail("hii/001", "ab001", listOf("kiosk"), listOf("/vital")))
            put("ab002", DeviceKeyDetail("hii/002", "ab002", listOf("kiosk"), listOf("/height")))
            put("ab003", DeviceKeyDetail("hii/003", "ab003", listOf("kiosk"), listOf("/bmi")))
        }
    }
}
