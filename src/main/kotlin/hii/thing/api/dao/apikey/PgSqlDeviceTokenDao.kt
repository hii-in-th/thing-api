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

import hii.thing.api.auth.DeviceToken
import hii.thing.api.auth.NotFoundToken
import hii.thing.api.dao.Now
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class PgSqlDeviceTokenDao(connection: () -> Connection) : DeviceTokenDao {

    init {
        Database.connect(connection)
    }

    override fun getDeviceBy(baseToken: String): DeviceToken {
        var deviceTokenOut: DeviceToken? = null
        transaction {
            SchemaUtils.create(SqlApiKeyStore)
            SqlApiKeyStore.select { SqlApiKeyStore.baseToken eq baseToken }.limit(1)
                .map { deviceTokenOut = mapResult(it) }
        }
        return deviceTokenOut ?: throw NotFoundToken("ไม่พบ Api key")
    }

    private fun mapResult(it: ResultRow): DeviceToken {
        return DeviceToken(
            it[SqlApiKeyStore.deviceName],
            it[SqlApiKeyStore.baseToken],
            it[SqlApiKeyStore.roles].toList(),
            it[SqlApiKeyStore.scope].toList(),
            it[SqlApiKeyStore.deviceId]
        )
    }

    override fun registerDevice(deviceToken: DeviceToken): DeviceToken {
        var deviceTokenOut: DeviceToken? = null
        transaction {
            SchemaUtils.create(SqlApiKeyStore)
            require(runCatching { get(deviceToken.deviceID) }.isFailure)
            SqlApiKeyStore.insert {
                it[deviceId] = deviceToken.deviceID
                it[deviceName] = deviceToken.deviceName
                it[baseToken] = deviceToken.baseToken
                it[roles] = deviceToken.roles.toStringRawText()
                it[scope] = deviceToken.scope.toStringRawText()
                it[time] = Now()
            }
            SqlApiKeyStore.select { SqlApiKeyStore.deviceId eq deviceToken.deviceID }.limit(1)
                .map { deviceTokenOut = mapResult(it) }
        }
        return deviceTokenOut!!
    }

    private fun get(deviceId: String): DeviceToken {
        var deviceTokenOut: DeviceToken? = null
        transaction {
            SchemaUtils.create(SqlApiKeyStore)
            SqlApiKeyStore.select { SqlApiKeyStore.deviceId eq deviceId }.map { deviceTokenOut = mapResult(it) }
        }
        return deviceTokenOut!!
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
