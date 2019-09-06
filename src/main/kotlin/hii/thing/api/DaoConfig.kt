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

package hii.thing.api

import hii.thing.api.auth.dao.devicekey.DeviceKeyDao
import hii.thing.api.auth.dao.devicekey.InMemoryDeviceKeyDao
import hii.thing.api.auth.dao.devicekey.JwtDeviceKeyDao
import hii.thing.api.device.dao.DeviceDao
import hii.thing.api.device.dao.InMemoryDeviceDao
import hii.thing.api.device.dao.PgSqlDeviceDao
import hii.thing.api.security.keypair.dao.DemoRSAKeyPairDao
import hii.thing.api.security.keypair.dao.RSAKeyPairDao
import hii.thing.api.security.keypair.dao.StringRSAKeyPairDao
import hii.thing.api.sessions.dao.InMemorySessionDao
import hii.thing.api.sessions.dao.RedisSessionDao
import hii.thing.api.sessions.dao.SessionsDao
import hii.thing.api.sessions.dao.recordsession.InMemoryRecordSessionDao
import hii.thing.api.sessions.dao.recordsession.PgSqlRecordSessionDao
import hii.thing.api.sessions.dao.recordsession.RecordSessionDao
import hii.thing.api.sessions.dao.recordsession.toSqlTime
import hii.thing.api.vital.dao.bp.BloodPressuresDao
import hii.thing.api.vital.dao.bp.InMemoryBloodPressuresDao
import hii.thing.api.vital.dao.bp.PgSqlBloodPressuresDao
import hii.thing.api.vital.dao.height.HeightsDao
import hii.thing.api.vital.dao.height.InMemoryHeightsDao
import hii.thing.api.vital.dao.height.PgSqlHeightsDao
import hii.thing.api.vital.dao.lastresult.InMemoryLastResultDao
import hii.thing.api.vital.dao.lastresult.LastResultDao
import hii.thing.api.vital.dao.lastresult.PgSqlLastResultDao
import hii.thing.api.vital.dao.weight.InMemoryWeightDao
import hii.thing.api.vital.dao.weight.PgSqlWeightDao
import hii.thing.api.vital.dao.weight.WeightDao
import org.joda.time.DateTime
import redis.clients.jedis.HostAndPort
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

/**
 * หากกำหนดค่า HII_ALONE ใน System env จะทำงานแบบ in memory ทั้งหมด
 */
var standalone = property("HII_ALONE") != null

// sql config
const val SQL_SESSION_LENGTH = 36
const val SQL_DEVICE_ID_LENGTH = 36
val dataSourcePool by lazy { PoolDataSource() }

// Database configuration
val dbUrl by lazy { property("DB_URL") }
val dbUsername by lazy { property("DB_USER") }
val dbPassword by lazy { property("DB_PASSWORD") }
val dbDriver = "org.postgresql.Driver"
val dbProperties by lazy {
    hashMapOf(
        "MinPoolSize" to "0",
        "MaxPoolSize" to "10",
        "MaxIdleTime" to "0"
    )
}

// Redis configuration.
val redisHost by lazy { property("RE_HOST") }
val redisPort by lazy { property("RE_PORT").toInt() }
val redisExpireSec by lazy { property("RE_EXPIRE_SEC").toInt() }

val timeZone = ZoneId.of("Asia/Bangkok")!!

val rsaPrivateKey by lazy { property("HII_PRIVATE") }
val rsaPublicKey by lazy { property("HII_PUBLIC") }

const val refResultLinkLength = 16

/**
 * คำสั่งในการสร้างโครงสร้าง Database ในรูปแบบ SQL
 */
val sqlSequentCreateDao = {
    getDao<DeviceDao>()
    getDao<RecordSessionDao>()
}

/**
 * เกี่ยวกับการสร้าง Dao ใช้แยกระหว่าง standalone กับ production
 * @see standalone
 */
inline fun <reified T : Dao> getDao(): T {
    val dao = when (T::class) {
        DeviceKeyDao::class -> if (standalone) InMemoryDeviceKeyDao() else JwtDeviceKeyDao()
        RecordSessionDao::class -> if (standalone) InMemoryRecordSessionDao() else PgSqlRecordSessionDao { dataSourcePool.getConnection() }
        SessionsDao::class -> if (standalone) InMemorySessionDao() else RedisSessionDao(
            setOf(HostAndPort(redisHost, redisPort)), redisExpireSec
        )
        LastResultDao::class -> if (standalone) InMemoryLastResultDao() else PgSqlLastResultDao { dataSourcePool.getConnection() }
        BloodPressuresDao::class -> if (standalone) InMemoryBloodPressuresDao() else PgSqlBloodPressuresDao { dataSourcePool.getConnection() }
        HeightsDao::class -> if (standalone) InMemoryHeightsDao() else PgSqlHeightsDao { dataSourcePool.getConnection() }
        WeightDao::class -> if (standalone) InMemoryWeightDao() else PgSqlWeightDao { dataSourcePool.getConnection() }
        RSAKeyPairDao::class -> if (standalone) DemoRSAKeyPairDao() else StringRSAKeyPairDao(
            rsaPrivateKey,
            rsaPublicKey
        )
        DeviceDao::class -> if (standalone) InMemoryDeviceDao() else PgSqlDeviceDao { dataSourcePool.getConnection() }
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

private fun property(keyName: String): String {
    System.getProperty(keyName)?.let { return it }
    return System.getenv(keyName) ?: "null"
}
