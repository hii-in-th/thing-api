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

import hii.thing.api.dao.apikey.DeviceTokenDao
import hii.thing.api.dao.apikey.InMemoryDeviceTokenDao
import hii.thing.api.dao.apikey.JwtDeviceTokenDao
import hii.thing.api.dao.keyspair.InMemoryRSAKeyPairDao
import hii.thing.api.dao.keyspair.RSAKeyPairDao
import hii.thing.api.dao.keyspair.StringRSAKeyPairDao
import hii.thing.api.dao.lastresult.InMemoryLastResultDao
import hii.thing.api.dao.lastresult.LastResultDao
import hii.thing.api.dao.lastresult.PgSqlLastResultDao
import hii.thing.api.dao.registerstore.InMemoryRegisterStoreDao
import hii.thing.api.dao.registerstore.PgSqlRegisterStoreDao
import hii.thing.api.dao.registerstore.RegisterStoreDao
import hii.thing.api.dao.registerstore.toSqlTime
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
import java.time.ZoneId
import java.util.TimeZone

/**
 * หากกำหนดค่า HII_ALONE ใน System env จะทำงานแบบ in memory ทั้งหมด
 */
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

val timeZone = ZoneId.of("Asia/Bangkok")!!

val rsaPrivateKey by lazy { System.getenv("HII_PRIVATE") }
val rsaPublicKey by lazy { System.getenv("HII_PUBLIC") }

const val refResultLinkLength = 16

/**
 * เกี่ยวกับการสร้าง Dao ใช้แยกระหว่าง standalone กับ production
 * @see standalone
 */
inline fun <reified T : Dao> getDao(): T {
    val dao = when (T::class) {
        DeviceTokenDao::class -> if (standalone) InMemoryDeviceTokenDao() else JwtDeviceTokenDao()
        RegisterStoreDao::class -> if (standalone) InMemoryRegisterStoreDao() else PgSqlRegisterStoreDao { dataSourcePool.getConnection() }
        SessionsDao::class -> if (standalone) InMemorySessionDao() else RedisSessionDao(
            setOf(HostAndPort(redisHost, redisPort)), redisExpireSec
        )
        LastResultDao::class -> if (standalone) InMemoryLastResultDao() else PgSqlLastResultDao { dataSourcePool.getConnection() }
        BloodPressuresDao::class -> if (standalone) InMemoryBloodPressuresDao() else PgSqlBloodPressuresDao { dataSourcePool.getConnection() }
        HeightsDao::class -> if (standalone) InMemoryHeightsDao() else PgSqlHeightsDao { dataSourcePool.getConnection() }
        WeightDao::class -> if (standalone) InMemoryWeightDao() else PgSqlWeightDao { dataSourcePool.getConnection() }
        RSAKeyPairDao::class -> if (standalone) InMemoryRSAKeyPairDao() else StringRSAKeyPairDao(
            rsaPrivateKey,
            rsaPublicKey
        )
        else -> throw TypeCastException("Cannot type dao.")
    }
    return dao as T
}

fun Now() = LocalDateTime.now(ZoneId.of("Universal")).toSqlTime()

/**
 * แปลงเวลา joda time เป็น java time เนื่องจาก
 * lib ที่ใช้เชื่อมกับ database ใช้เป็น joda time
 * @see org.jetbrains.exposed.sql.Table.datetime
 */
internal fun DateTime.toJavaTime(): LocalDateTime = LocalDateTime.of(
    this.year,
    this.monthOfYear,
    this.dayOfMonth,
    this.hourOfDay,
    this.minuteOfHour,
    this.secondOfMinute,
    this.millisOfSecond * 1000000
).plusSeconds(TimeZone.getTimeZone(timeZone).rawOffset / 1000L)
