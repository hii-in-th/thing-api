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

import hii.thing.api.Now
import hii.thing.api.device.Device
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.sql.Connection

class PgSqlDeviceDao(connection: () -> Connection) : DeviceDao {
    init {
        Database.connect(connection)
        transaction { SchemaUtils.create(SqlDevice) }
    }

    override fun create(device: Device): Device {
        var deviceOut: Device? = null

        transaction {
            require(device.deviceId != null) { "Device id is not null" }
            require(device.type != null) { "Device type is not null" }
            require(runCatching { get(device.deviceId!!) }.isFailure) {
                "ไม่สามารถสร้าง device ซ้ำที่มีอยู่ได้จำเป็นต้องใช้ update"
            }
            val now = Now()
            SqlDevice.insert {
                it[deviceId] = device.deviceId!!
                it[create] = now
                it[update] = now
                it[location] = device.location
                it[type] = device.type!!
            }
            deviceOut = get(device.deviceId!!)
        }
        return deviceOut!!
    }

    override fun update(deviceId: String, device: Device): Device {
        var deviceOut: Device? = null
        require(deviceId == device.deviceId)
        require(device.type != null)
        try {
            transaction {
                SqlDevice.update({ SqlDevice.deviceId eq deviceId }) {
                    it[SqlDevice.deviceId] = deviceId
                    it[update] = Now()
                    it[location] = device.location
                    it[type] = device.type!!
                }
                deviceOut = get(deviceId)
            }
        } catch (ex: ExposedSQLException) {
            require(false)
        }
        require(deviceOut != null)
        return deviceOut!!
    }

    override fun get(deviceId: String): Device {
        var deviceOut: Device? = null
        try {
            transaction {
                SqlDevice.select { SqlDevice.deviceId eq deviceId }
                    .limit(1).map {
                        deviceOut = SqlDevice.mapResult(it)
                    }
            }
        } catch (ex: ExposedSQLException) {
            if ((ex.message ?: "").contains("ERROR: relation \"device\" does not exist"))
                require(false)
        }
        require(deviceOut != null)
        return deviceOut!!
    }
}
