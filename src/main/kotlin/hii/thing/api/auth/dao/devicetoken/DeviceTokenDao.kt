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

package hii.thing.api.auth.dao.devicetoken

import hii.thing.api.Dao
import hii.thing.api.auth.DeviceToken

/**
 * สำหรับสร้าง และ ดึงข้อมูลของ Device จาก Token
 */
interface DeviceTokenDao : Dao {
    fun getDeviceBy(baseToken: String): DeviceToken
    fun registerDevice(deviceToken: DeviceToken): DeviceToken
}