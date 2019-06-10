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

package hii.thing.api.dao.pgsql

class Postgresql(pgUrl: String, username: String, password: String) {
    init {
        Database.connect(
            url = pgUrl,
            driver = "org.postgresql.Driver",
            user = username,
            password = password
        )
    }

    fun add(message: String) {
        transaction {
            SchemaUtils.create(Message)
            val time = Message.insert {
                it[time] = DateTime.now()
                it[Message.message] = message
            } get Message.time
        }
    }

    fun get(): Map<DateTime, String> {
        val output = hashMapOf<DateTime, String>()
        transaction {
            for (message in Message.selectAll()) {
                output[message[Message.time]] = message[Message.message]
            }
        }
        return output.toMap()
    }

    fun remove(message: String) {
        transaction {
            Message.deleteWhere {
                Message.message like message
            }
        }
    }

    fun removeAll() {
        transaction {
            Message.deleteAll()
        }
    }
}
