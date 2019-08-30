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

package hii.thing.api.vital.dao.height

import hii.thing.api.SQL_SESSION_LENGTH
import hii.thing.api.toJavaTime
import hii.thing.api.vital.Height
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

internal object SqlHeight : Table("height") {
    val sessionId = varchar("session_id", SQL_SESSION_LENGTH).primaryKey(0).primaryKey(1)
    val time = datetime("time").primaryKey(0)
    var height = float("height")

    fun getResult(it: ResultRow): Height {
        return Height(
            it[height],
            it[sessionId],
            it[time].toJavaTime()
        )
    }
}
