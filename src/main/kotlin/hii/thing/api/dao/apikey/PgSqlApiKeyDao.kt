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
import hii.thing.api.dao.ApiKeyDao
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class PgSqlApiKeyDao(pgUrl: String, username: String, password: String) : ApiKeyDao {

    init {
        Database.connect(
            url = pgUrl,
            driver = "org.postgresql.Driver",
            user = username,
            password = password
        )
    }

    override fun getDeviceBy(baseToken: String): Device {
        var deviceOut: Device? = null
        transaction {
            SqlDevice.select { SqlDevice.baseToken eq baseToken }.limit(1)
                .map { deviceOut = mapResult(it) }
        }
        return deviceOut!!
    }

    private fun mapResult(it: ResultRow): Device {
        return Device(
            it[SqlDevice.deviceName],
            it[SqlDevice.baseToken],
            it[SqlDevice.audience],
            it[SqlDevice.roles].toList(),
            it[SqlDevice.scope].toList(),
            it[SqlDevice.deviceId]
        )
    }

    override fun registerDevice(device: Device): Device {
        var deviceOut: Device? = null
        transaction {
            SchemaUtils.create(SqlDevice)
            SqlDevice.insert {
                it[deviceId] = device.deviceID
                it[deviceName] = device.deviceName
                it[baseToken] = device.baseToken
                it[audience] = device.audience
                it[roles] = device.roles.toStringRawText()
                it[scope] = device.scope.toStringRawText()
            }
            SqlDevice.select { SqlDevice.deviceId eq device.deviceID }.limit(1)
                .map { deviceOut = mapResult(it) }
        }
        return deviceOut!!
    }

    private fun List<String>.toStringRawText(): String {
        var stringRawText = ""
        forEach {
            stringRawText = "$stringRawText$it,"
        }
        return stringRawText
    }

    private fun String.toList(): List<String> {
        val list = arrayListOf<String>()
        split(",").forEach {
            if (it.isNotBlank())
                list.add(it)
        }
        return list.toList()
    }
}
