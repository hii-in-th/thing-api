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

package hii.thing.api.vital

import java.time.LocalDateTime
import java.util.LinkedList

data class Result(
    val age: Int?,
    val height: Float?,
    val weight: Float?,

    val bloodPressure: BloodPressures?
) {
    var refLink: String? = null
    val bmi: Float? = bmiCal()

    fun bmiCal() =
        if (height != null && weight != null) weight / ((height / 100) * (height / 100)) else null

    var suggestions: List<Suggestions>? = calSuggestions(this)
}

data class Suggestions(
    val imgUrl: String?,
    val text: String?,
    val causes: List<String>?
)

fun calSuggestions(result: Result): List<Suggestions>? {
    val list = LinkedList<Suggestions>().apply {
        add(Suggestions(null, "อายุ ${result.age}", null))
        add(Suggestions(null, "BMI ${result.bmiCal()}", null))
    }
    // TODO ("Cal suggestions")

    return list.toList().takeIf { it.isNotEmpty() }
}

fun calAge(date: LocalDateTime): Int {
    val currentDate = LocalDateTime.now()

    val partDateTime =
        currentDate.minusYears(date.year.toLong())
            .minusMonths(date.monthValue.toLong())
            .minusDays(date.dayOfMonth.toLong())

    return partDateTime.year
}
