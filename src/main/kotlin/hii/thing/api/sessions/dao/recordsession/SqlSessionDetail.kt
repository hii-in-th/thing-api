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

package hii.thing.api.sessions.dao.recordsession

import hii.thing.api.SQL_SESSION_LENGTH
import hii.thing.api.device.dao.SqlDevice
import org.jetbrains.exposed.sql.Table

internal object SqlSessionDetail : Table("session") {
    val sessionId = varchar("session_id", SQL_SESSION_LENGTH).primaryKey(0)
    val time = datetime("time")
    val deviceId = reference("device_id", SqlDevice.deviceId).index()
    val citizenId = varchar("citizen_id", 36).nullable()
    val citizenIdInput = varchar("citizen_id_input", 10).nullable()
    val birthDate = date("birth_date").nullable()
    val name = varchar("name", 100).nullable()
    val sex = varchar("sex", 10).nullable()
}
