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

package hii.thing.api.auth.dao.history

import hii.thing.api.Dao

interface DeviceIdMapToAccessId : Dao {
    /**
     * บันทึกการขอ auth
     * @param deviceId ของ device ที่ออกให้จะเป็น id ของ token key
     * @param accessId เป็น id ของ access token ที่ device key ออกให้
     */
    fun record(deviceId: String, accessId: String)

    /**
     * ค้นหาว่า id ว่า device ไหนเป็นคนออกให้
     * @param issuedTo หมายเลข id ของ issued
     */
    fun getDeviceIdBy(issuedTo: String): String
}
