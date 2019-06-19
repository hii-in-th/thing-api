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

import org.joda.time.DateTime
import java.time.LocalDateTime
import java.time.ZoneOffset

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

internal fun LocalDateTime.toSqlTime(): DateTime = DateTime(this.toEpochSecond(ZoneOffset.UTC))
internal fun DateTime.toJavaTime(): LocalDateTime = LocalDateTime.ofEpochSecond(this.millis, 0, ZoneOffset.UTC)
