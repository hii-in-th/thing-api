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

package hii.thing.api.sendnhso

import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.vital.BloodPressures
import hii.thing.api.vital.Height
import hii.thing.api.vital.Weight
import java.time.LocalDateTime
import java.time.ZoneId

data class SendMessage(
    val deviceId: String,
    val location: String,
    val ipAddress: String,
    val citizenInput: CreateSessionDetail.InputType,
    val citizenId: String? = null,
    val weight: Weight? = null,
    val height: Height? = null,
    val bp: BloodPressures? = null
) {
    val time: LocalDateTime = LocalDateTime.now(ZoneId.of("Universal"))
    fun toCsv(): String = "$time," +
        "$deviceId," +
        "$location," +
        "$ipAddress," +
        "$citizenInput," +
        "$citizenId," +
        "${weight?.weight}," +
        "${height?.height}," +
        "${bp?.sys}," +
        "${bp?.dia}," +
        "${bp?.pulse}"
            .replace("null", "")
}
