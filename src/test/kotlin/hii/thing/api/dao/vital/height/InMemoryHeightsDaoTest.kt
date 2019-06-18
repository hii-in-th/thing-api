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

package hii.thing.api.dao.vital.height

import hii.thing.api.vital.Height
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.junit.Before
import org.junit.Test

class InMemoryHeightsDaoTest {

    val memory = InMemoryHeightsDao()
    val heightsDao: HeightsDao = memory

    val sessionId = "max-999-888-111"
    val height = Height(170.1F)
    val height2 = Height(169.0F)

    @Test
    fun save() {
        val save = heightsDao.save(sessionId, height)

        save.sessionId!! `should be equal to` sessionId
        save.time `should be` height.time
        save.height `should be equal to` height.height
    }

    @Test(expected = Exception::class)
    fun saveDuplicate() {
        heightsDao.save(sessionId, height)
        heightsDao.save(sessionId, height2)
    }

    @Test
    fun getBy() {
        heightsDao.save(sessionId, height)
        val get = heightsDao.getBy(sessionId)

        get.sessionId!! `should be equal to` sessionId
        get.time `should be` height.time
        get.height `should be equal to` height.height
    }

    @Test(expected = Exception::class)
    fun getEmpty() {
        heightsDao.getBy(sessionId)
    }

    @Before
    fun setUp() {
        memory.cleanAll()
    }
}
