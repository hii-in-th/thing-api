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

import org.joda.time.DateTime
import java.time.LocalDateTime

internal fun String.toJavaTime(): LocalDateTime {
    val rex = Regex("""^(\d{4})-(\d{2})-(\d{2})$""")
    val result = rex.matchEntire(this)?.groupValues
    require(result != null) { "รูปแบบวันเกิดผิดรูปแบบ ควรใช้รูปแบบตามนี้ Ex.\"1976-06-05\"" }
    val year = result[1].toInt()
    val month = result[2].toInt()
    val day = result[3].toInt()

    return LocalDateTime.of(year, month, day, 0, 0)
}

internal fun DateTime.toStringDate(): String {
    val year = String.format("%04d", this.year)
    val month = String.format("%02d", this.monthOfYear)
    val day = String.format("%02d", this.dayOfMonth)
    return "$year-$month-$day"
}

internal fun LocalDateTime.toSqlTime(): DateTime =
    DateTime(
        this.year,
        this.monthValue,
        this.dayOfMonth,
        this.hour,
        this.minute,
        this.second,
        this.nano / 1000000
    )
