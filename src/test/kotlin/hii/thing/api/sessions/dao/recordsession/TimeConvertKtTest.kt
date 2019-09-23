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

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.joda.time.DateTime
import org.junit.Test
import java.time.LocalDateTime

class TimeConvertKtTest {
    val stringDate = "1976-06-05"
    val javaTime = LocalDateTime.of(1976, 6, 5, 0, 0)
    val sqlDateTime = DateTime(1976, 6, 5, 0, 0)

    @Test
    fun stringToJavaTime() {
        stringDate.toJavaTime() `should equal` javaTime
    }

    @Test
    fun javaTimeToSqlTime() {
        javaTime.toSqlTime() `should equal` sqlDateTime
    }

    @Test
    fun sqlDateTimeToStringDate() {
        sqlDateTime.toStringDate() `should be equal to` stringDate
    }
}
