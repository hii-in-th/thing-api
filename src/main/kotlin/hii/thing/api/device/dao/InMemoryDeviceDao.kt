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

import hii.thing.api.device.Device

class InMemoryDeviceDao : DeviceDao {
    private val storage = hashMapOf<String, Device>()
    override fun create(device: Device): Device {
        require(storage[device.deviceId] == null) { "ไม่สามารถ เพิ่มข้อมูลซ้ำได้ ต้องใช้ update" }
        storage[device.deviceId!!] = device
        return device
    }

    override fun update(deviceId: String, device: Device): Device {
        require(storage[deviceId] != null) { "ไม่พบ device id ที่ระบุ" }
        require(deviceId == device.deviceId)
        storage[device.deviceId!!] = device
        return device
    }

    override fun get(deviceId: String): Device {
        val device = storage[deviceId]
        require(device != null) { "ไม่พบ device id ที่ระบุ" }
        return device
    }

    fun clear() {
        storage.clear()
    }
}
