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

package hii.thing.api.sessions

import java.time.LocalDateTime

data class CreateSessionDetail(
    val deviceId: String,
    val citizenId: String? = null,
    val citizenIdInput: String? = null,
    val birthDate: String? = null,
    val name: String? = null,
    val sex: Sex? = null
) {
    val age: Int?
        get() = birthDateToAge(birthDate)

    internal fun birthDateToAge(birthDate: String?, currentDate: LocalDateTime = LocalDateTime.now()): Int? {
        if (birthDate == null) return null
        val rex = Regex("""^(\d{4})-(\d{2})-(\d{2})$""")
        val result = rex.matchEntire(birthDate)?.groupValues ?: return null

        val year = result[1].toLong()
        val month = result[2].toLong()
        val day = result[3].toLong()

        return currentDate
            .minusYears(year)
            .minusMonths(month)
            .minusDays(day)
            .year
    }

    enum class Sex() {
        MALE, FEMALE, UNKNOWN
    }
}
