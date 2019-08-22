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

/**
 * Object ผลของการวัด
 */
data class Result(
    val age: Int?,
    val height: Float?,
    val weight: Float?,

    val bloodPressure: BloodPressures?,
    val sex: String? = null
) {
    var replayLink: String? = null
    val bmi: Float? = bmiCal()

    fun bmiCal() =
        if (height != null && weight != null) weight / ((height / 100) * (height / 100)) else null

    var suggestions: List<Suggestions>? = thinkSuggestions()
}

private fun Result.thinkSuggestions(): List<Suggestions>? {
    val list = LinkedList<Suggestions>()

    bmiCal()?.let { list.add(checkBMI(it)) }


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

private fun checkBMI(bmi: Float): Suggestions {
    val message =
        when {
            bmi <= 18.5 ->
                "คุณดูผอมเกินไปอาจเกิดจากการได้รับสารอาหารไม่เพียงพอ หรือ " +
                    "ทานอาหารทีให้พลังงานไม่เพียงพอ ส่งผลให้รู้สึกอ่อนเพลียได้ง่าย" +
                    "ควรออกกำลังกายเสริม และรับประทานอาหารให้เพียงพอ จะช่วยได้"
            bmi <= 24 ->
                "คุณดูเหมาะสมแล้ว ค่า BMI อยู่ในช่วงปกติ ควรรักษาให้อยู่ในจุดนี้" +
                    "ให้ได้นานที่สุด เพื่อสุขภาพที่ดี"
            bmi <= 29.9 ->
                "ในศัพท์ทางการจะเรียกว่า อ้วนระดับหนึ่ง ถึงแม้จะไม่อ้วนมาก แต่ก็ยัง" +
                    "มีความเสียงโรคในกลุ่ม NCDs เช่น เบาหวาน ความดัน" +
                    "ควรปรับพฤติกรรมการทานอาหาร ให้เหมาะสม " +
                    "และออกกำลังกายเพื่อสุขภาพ" +
                    "ควรทานอาหารที่เหมาะสม ครบ 5 หมู่ ในปริมาณที่เหมาะสม" +
                    "ห้ามอดอาหาร เด็ดขาด " +
                    "เพราะการอดอาหาร จะทำให้สุขภาพเสีย และกลับไปโยโย่"
            else ->
                "อ้วนมาก มีความอันตราย เสี่ยงที่จะเกิดโรคร้ายแรงที่แฝงมากับความอ้วน" +
                    "ควรไปตรวจสุขภาพ และต้องปรับพฤติกรรมการกิน และควรเริ่มออกกำลังกาย" +
                    "พยายามเดินให้เยอะ หรือ ขยับร่างกายบ่อยๆ" +
                    "และทานอาหารที่เหมาะสม ครบ 5 หมู่ ในปริมาณที่เหมาะสม" +
                    "ห้ามอดอาหาร เด็ดขาด " +
                    "เพราะการอดอาหาร จะทำให้สุขภาพเสีย และกลับไปโยโย่"
        }

    return Suggestions(null, message, listOf("ส่วนสูง", "น้ำหนัก"))
}
