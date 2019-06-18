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

package hii.thing.api.dao.registerstore

import hii.thing.api.dao.SQL_SESSION_LENGTH
import org.jetbrains.exposed.sql.Table

internal object SqlRegisterDetail : Table("register") {
    val sessionId = varchar("sessionid", SQL_SESSION_LENGTH).primaryKey(0).primaryKey(1)
    val time = datetime("time").primaryKey(0)
    val deviceId = varchar("deviceid", 36)
    val citizenId = varchar("citizenid", 36).nullable()
    val citizenIdInput = varchar("citizenidinput", 10).nullable()
    val birthDate = varchar("birthdate", 10).nullable()
}
