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

class BloodPressures(
    val sys: Float,
    val dia: Float,
    var sessionId: String? = null,
    val time: LocalDateTime = LocalDateTime.now()
) {
    val level = calLevel()

    internal fun calLevel(): BloodLevel {
        val systolic = sys
        val diastolic = dia
        val out = mutableSetOf<BloodLevel>()

        if (systolic < 90 || diastolic < 60) out.add(BloodLevel.isLow)
        if (systolic in 90.0..119.0 && diastolic in 60.0..79.0) out.add(BloodLevel.isNormal)
        if (systolic >= 140.0 || diastolic >= 90.0) out.add(BloodLevel.isHigh)
        if ((systolic in 120.0..139.0 || diastolic in 80.0..89.0) && !out.contains(BloodLevel.isHigh)) out.add(
            BloodLevel.isPreHigh
        )
        return out.first()
    }

    enum class BloodLevel {
        isLow, isNormal, isPreHigh, isHigh
    }
}
