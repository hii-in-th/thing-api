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

package hii.thing.api.dao

import hii.thing.api.dao.apikey.ApiKeyDao
import hii.thing.api.dao.apikey.InMemoryApiKeyDao
import hii.thing.api.dao.apikey.PgSqlApiKeyDao
import hii.thing.api.dao.keyspair.InMemoryRSAKeyPairDao
import hii.thing.api.dao.keyspair.RSAKeyPairDao
import hii.thing.api.dao.keyspair.RedisRSAKeyPairDao
import hii.thing.api.dao.registerstore.InMemoryRegisterStoreDao
import hii.thing.api.dao.registerstore.PgSqlRegisterStoreDao
import hii.thing.api.dao.registerstore.RegisterStoreDao
import hii.thing.api.dao.session.InMemorySessionDao
import hii.thing.api.dao.session.RedisSessionDao
import hii.thing.api.dao.session.SessionsDao
import hii.thing.api.dao.vital.bp.BloodPressuresDao
import hii.thing.api.dao.vital.bp.InMemoryBloodPressuresDao
import hii.thing.api.dao.vital.bp.PgSqlBloodPressuresDao
import hii.thing.api.dao.vital.height.HeightsDao
import hii.thing.api.dao.vital.height.InMemoryHeightsDao
import hii.thing.api.dao.vital.height.PgSqlHeightsDao
import hii.thing.api.dao.vital.weight.InMemoryWeightDao
import hii.thing.api.dao.vital.weight.PgSqlWeightDao
import hii.thing.api.dao.vital.weight.WeightDao
import org.joda.time.DateTime
import redis.clients.jedis.HostAndPort
import java.time.LocalDateTime
import java.time.ZoneOffset

val standalone = System.getenv("HII_ALONE") != null
// sql config
const val SQL_SESSION_LENGTH = 36
val dataSourcePool by lazy { DataSource() }

// Database configuration
val dbUrl by lazy { System.getenv("DB_URL") }
val dbUsername by lazy { System.getenv("DB_USER") }
val dbPassword by lazy { System.getenv("DB_PASSWORD") }
val dbDriver = "org.postgresql.Driver"
val dbProperties by lazy {
    hashMapOf(
        "MinPoolSize" to "0",
        "MaxPoolSize" to "10",
        "MaxIdleTime" to "0"
    )
}

// Redis configuration.
val redisHost by lazy { System.getenv("RE_HOST") }
val redisPort by lazy { System.getenv("RE_PORT").toInt() }
val redisExpireSec by lazy { System.getenv("RE_EXPIRE_SEC").toInt() }

// Redis rsa store server ถ้าไม่กำหนดจะใช้ค่าเดียวกับ ด้านบน
val redisKeyHost by lazy { System.getenv("RE_KEY_HOST") }
val redisKeyPort by lazy { System.getenv("RE_KEY_PORT")?.toInt() }

inline fun <reified T : Dao> getDao(): T {
    val dao = when (T::class) {
        ApiKeyDao::class -> if (standalone) InMemoryApiKeyDao() else PgSqlApiKeyDao { dataSourcePool.getConnection() }
        RegisterStoreDao::class -> if (standalone) InMemoryRegisterStoreDao() else PgSqlRegisterStoreDao { dataSourcePool.getConnection() }
        SessionsDao::class -> if (standalone) InMemorySessionDao() else RedisSessionDao(
            setOf(HostAndPort(redisHost, redisPort)), redisExpireSec
        )
        BloodPressuresDao::class -> if (standalone) InMemoryBloodPressuresDao() else PgSqlBloodPressuresDao { dataSourcePool.getConnection() }
        HeightsDao::class -> if (standalone) InMemoryHeightsDao() else PgSqlHeightsDao { dataSourcePool.getConnection() }
        WeightDao::class -> if (standalone) InMemoryWeightDao() else PgSqlWeightDao { dataSourcePool.getConnection() }
        RSAKeyPairDao::class -> if (standalone) InMemoryRSAKeyPairDao() else {
            RedisRSAKeyPairDao(
                setOf(
                    HostAndPort(
                        redisKeyHost ?: redisHost,
                        redisKeyPort ?: redisPort
                    )
                )
            )
        }
        else -> throw TypeCastException("Cannot type dao.")
    }
    return dao as T
}

internal fun DateTime.toJavaTime(): LocalDateTime = LocalDateTime.ofEpochSecond(this.millis, 0, ZoneOffset.UTC)
