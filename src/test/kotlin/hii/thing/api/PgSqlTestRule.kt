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

import hii.thing.api.dao.DataSource
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.postgresql.util.PSQLException
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres
import java.sql.Connection

class PgSqlTestRule<T : Table>(private val table: T) : TestRule {
    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            try {
                // before()
                base.evaluate()
            } finally {
                after()
            }
        }
    }

    private fun after() {
        try {
            transaction {
                SchemaUtils.drop(table)
            }
        } catch (ex: PSQLException) {
            pgSql.stop()
            pgSql = EmbeddedPostgres()
            url = pgSql.start()
        }
    }

    companion object {
        var count = 1
        lateinit var pgSql: EmbeddedPostgres
        var dataSource: DataSource? = null
        var url: String = ""
    }

    val connection = {
        count++
        getLogger().debug { "Count $count" }
        if (count % 10 == 0)
            Thread.sleep(1000)
        try {
            dataSource!!.getConnection()
        } catch (ex: KotlinNullPointerException) {
            createNewConnection()
        }
    }

    private fun createNewConnection(): Connection {
        pgSql = EmbeddedPostgres()
        url = pgSql.start()
        dataSource = DataSource(url, "postgres", "postgres", 1)
        return dataSource!!.getConnection()
    }
}
