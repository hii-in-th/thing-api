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

import hii.thing.api.vital.BloodPressures.BloodLevel.HYPERTENSION
import hii.thing.api.vital.BloodPressures.BloodLevel.HYPERTENSION_CRISIS
import hii.thing.api.vital.BloodPressures.BloodLevel.NORMAL
import hii.thing.api.vital.BloodPressures.BloodLevel.RISK
import org.amshove.kluent.`should equal`
import org.junit.Test

class BloodPressuresLevelTest {
    @Test
    fun normal() {
        BloodPressures(119F, 79F).level `should equal` NORMAL
        BloodPressures(110F, 65F).level `should equal` NORMAL
    }

    @Test
    fun risk() {
        BloodPressures(120F, 79F).level `should equal` RISK
        BloodPressures(129F, 65F).level `should equal` RISK
    }

    @Test
    fun hypertension() {
        BloodPressures(120F, 80F).level `should equal` HYPERTENSION
        BloodPressures(130F, 65F).level `should equal` HYPERTENSION
        BloodPressures(140F, 76F).level `should equal` HYPERTENSION
        BloodPressures(160F, 79F).level `should equal` HYPERTENSION

        BloodPressures(119F, 85F).level `should equal` HYPERTENSION
        BloodPressures(115F, 90F).level `should equal` HYPERTENSION
        BloodPressures(110F, 100F).level `should equal` HYPERTENSION
    }

    @Test
    fun hypertensionCrisis() {
        BloodPressures(180F, 80F).level `should equal` HYPERTENSION_CRISIS
        BloodPressures(190F, 65F).level `should equal` HYPERTENSION_CRISIS
        BloodPressures(110F, 120F).level `should equal` HYPERTENSION_CRISIS
        BloodPressures(115F, 130F).level `should equal` HYPERTENSION_CRISIS
    }
}
