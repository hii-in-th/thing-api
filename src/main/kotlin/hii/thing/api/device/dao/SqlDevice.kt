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

import hii.thing.api.SQL_DEVICE_ID_LENGTH
import hii.thing.api.device.Device
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

internal object SqlDevice : Table("device") {
    val deviceId = varchar("device_id", SQL_DEVICE_ID_LENGTH).primaryKey()
    val deviceName = varchar("device_name", 100)
    val create = datetime("create")
    val update = datetime("update")
    val location = varchar("location", 255)
    val type = varchar("type", 36)

    fun mapResult(result: ResultRow): Device {
        return Device(
            result[location],
            result[deviceId],
            result[deviceName],
            result[type]
        )
    }
}
