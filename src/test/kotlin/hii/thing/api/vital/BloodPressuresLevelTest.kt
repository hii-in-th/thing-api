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

import hii.thing.api.vital.BloodPressures.BloodLevel.isHigh
import hii.thing.api.vital.BloodPressures.BloodLevel.isLow
import hii.thing.api.vital.BloodPressures.BloodLevel.isNormal
import hii.thing.api.vital.BloodPressures.BloodLevel.isPreHigh
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not contain`
import org.junit.Test

class BloodPressuresLevelTest {
    @Test
    fun normal() {
        val bp = BloodPressures(90F, 60F).calLevel()

        bp `should contain` isNormal
        bp `should not contain` isLow
        bp `should not contain` isHigh
        bp `should not contain` isPreHigh
    }

    @Test
    fun normal2() {
        val bp = BloodPressures(119F, 79F).calLevel()

        bp `should contain` isNormal
        bp `should not contain` isLow
        bp `should not contain` isHigh
        bp `should not contain` isPreHigh
    }

    @Test
    fun low() {
        val bp = BloodPressures(60F, 50F).calLevel()

        bp `should not contain` isNormal
        bp `should contain` isLow
        bp `should not contain` isHigh
        bp `should not contain` isPreHigh
    }

    @Test
    fun preHigh() {
        val bp = BloodPressures(120F, 80F).calLevel()

        bp `should not contain` isNormal
        bp `should not contain` isLow
        bp `should not contain` isHigh
        bp `should contain` isPreHigh
    }

    @Test
    fun preHigh2() {
        val bp = BloodPressures(125F, 85F).calLevel()

        bp `should not contain` isNormal
        bp `should not contain` isLow
        bp `should not contain` isHigh
        bp `should contain` isPreHigh
    }

    @Test
    fun preHigh3() {
        val bp = BloodPressures(139F, 89F).calLevel()

        bp `should not contain` isNormal
        bp `should not contain` isLow
        bp `should not contain` isHigh
        bp `should contain` isPreHigh
    }

    @Test
    fun heigh() {
        val bp = BloodPressures(140F, 99F).calLevel()

        bp `should not contain` isNormal
        bp `should not contain` isLow
        bp `should contain` isHigh
        bp `should not contain` isPreHigh
    }

    @Test
    fun heigh2() {
        val bp = BloodPressures(180F, 110F).calLevel()

        bp `should not contain` isNormal
        bp `should not contain` isLow
        bp `should contain` isHigh
        bp `should not contain` isPreHigh
    }
}
