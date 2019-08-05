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

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.SQLException

/**
 * ตัวจัดการ DataSource ที่ใช้เชื่อมต่อกับ Database
 */
class DataSource(
    url: String = dbUrl,
    username: String = dbUsername,
    password: String = dbPassword,
    maxPool: Int = 10
) {

    private val ds: HikariDataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = url.trim()
        this.username = username
        this.password = password
        this.driverClassName = dbDriver
        addDataSourceProperty("dataSourceClassName", dbDriver)
        this.maximumPoolSize = maxPool
        dbProperties.forEach { (key, value) -> addDataSourceProperty(key, value) }
    })

    @Throws(SQLException::class)
    fun getConnection(): Connection {
        return ds.connection
    }
}
