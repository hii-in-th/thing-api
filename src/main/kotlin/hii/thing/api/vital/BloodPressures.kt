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

data class BloodPressures(
    val sys: Float,
    val dia: Float,
    var sessionId: String? = null,
    val time: LocalDateTime = LocalDateTime.now()
) {

    // TODO ประเมินความดัน
    // https://www.heart.org/en/health-topics/high-blood-pressure/understanding-blood-pressure-readings

    val level: BloodLevel
        get() = calLevel()

    internal fun calLevel(): BloodLevel {
        return if (sys < 120 && dia < 80) BloodLevel.NORMAL
        else if (sys <= 129 && dia < 80) BloodLevel.RISK
        else if (sys >= 180 || dia >= 120) BloodLevel.HYPERTENSION_CRISIS
        else BloodLevel.HYPERTENSION
    }

    enum class BloodLevel {
        NORMAL, RISK, HYPERTENSION, HYPERTENSION_CRISIS
    }
}
