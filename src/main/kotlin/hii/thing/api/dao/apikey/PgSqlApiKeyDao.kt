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
import hii.thing.api.dao.Now
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class PgSqlApiKeyDao(connection: () -> Connection) : ApiKeyDao {

    init {
        Database.connect(connection)
    }

    override fun getDeviceBy(baseToken: String): Device {
        var deviceOut: Device? = null
        transaction {
            SchemaUtils.create(SqlApiKeyStore)
            SqlApiKeyStore.select { SqlApiKeyStore.baseToken eq baseToken }.limit(1)
                .map { deviceOut = mapResult(it) }
        }
        return deviceOut ?: throw NotFoundToken("ไม่พบ Api key")
    }

    private fun mapResult(it: ResultRow): Device {
        return Device(
            it[SqlApiKeyStore.deviceName],
            it[SqlApiKeyStore.baseToken],
            it[SqlApiKeyStore.roles].toList(),
            it[SqlApiKeyStore.scope].toList(),
            it[SqlApiKeyStore.deviceId]
        )
    }

    override fun registerDevice(device: Device): Device {
        var deviceOut: Device? = null
        transaction {
            SchemaUtils.create(SqlApiKeyStore)
            require(runCatching { get(device.deviceID) }.isFailure)
            SqlApiKeyStore.insert {
                it[deviceId] = device.deviceID
                it[deviceName] = device.deviceName
                it[baseToken] = device.baseToken
                it[roles] = device.roles.toStringRawText()
                it[scope] = device.scope.toStringRawText()
                it[time] = Now()
            }
            SqlApiKeyStore.select { SqlApiKeyStore.deviceId eq device.deviceID }.limit(1)
                .map { deviceOut = mapResult(it) }
        }
        return deviceOut!!
    }

    private fun get(deviceId: String): Device {
        var deviceOut: Device? = null
        transaction {
            SchemaUtils.create(SqlApiKeyStore)
            SqlApiKeyStore.select { SqlApiKeyStore.deviceId eq deviceId }.map { deviceOut = mapResult(it) }
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
