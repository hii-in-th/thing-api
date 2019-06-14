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

package hii.thing.api.auth.jwt

import hii.thing.api.auth.Device
import hii.thing.api.auth.NotFoundToken

class DemoApiKeyDao : ApiKeyDao {
    override fun getDeviceBy(baseToken: String): Device {
        return when (baseToken) {
            "abcde" -> Device("hii/007", "abcde", "api.ffc.in.th", listOf("kios"), listOf("/vital", "/height", "/bmi"))
            "ab001" -> Device("hii/001", "ab001", "api.ffc.in.th", listOf("kios"), listOf("/vital"))
            "ab002" -> Device("hii/002", "ab002", "api.ffc.in.th", listOf("kios"), listOf("/height"))
            "ab003" -> Device("hii/003", "ab003", "api.ffc.in.th", listOf("kios"), listOf("/bmi"))
            else -> throw NotFoundToken("ไม่พบ Token")
        }
    }
}
