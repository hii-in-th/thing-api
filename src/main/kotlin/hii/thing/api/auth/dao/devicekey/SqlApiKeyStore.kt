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

import hii.thing.api.device.dao.SqlDevice
import org.jetbrains.exposed.sql.Table

internal object SqlApiKeyStore : Table("keystore") {
    val deviceId = reference("device_id", SqlDevice.deviceId).index()
    val time = datetime("time").primaryKey(0)
    val deviceName = varchar("name", 255) // sub
    val baseToken = varchar("api_key", 500)
    val roles = varchar("roles", 255)
    val scope = varchar("scope", 255)
}
