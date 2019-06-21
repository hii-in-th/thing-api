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
import hii.thing.api.auth.NotFoundToken

class InMemoryApiKeyDao : ApiKeyDao {
    override fun getDeviceBy(baseToken: String): Device {
        return storage[baseToken] ?: throw NotFoundToken("ไม่พบ Api key")
    }

    override fun registerDevice(device: Device): Device {
        storage[device.baseToken] = device
        return device
    }

    companion object {
        private val storage = linkedMapOf<String, Device>().apply {
            put("acde", Device("hii/7", "acde", listOf("kios"), listOf("/vital", "/height", "/bmi")))
            put("ab001", Device("hii/001", "ab001", listOf("kios"), listOf("/vital")))
            put("ab002", Device("hii/002", "ab002", listOf("kios"), listOf("/height")))
            put("ab003", Device("hii/003", "ab003", listOf("kios"), listOf("/bmi")))
        }
    }
}
