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

package hii.thing.api.dao.history.height

import hii.thing.api.`should be equal to`
import hii.thing.api.dao.history.BaseTestHistory
import hii.thing.api.vital.Height
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test

class InmemoryHistoryHeightsDaoTest : BaseTestHistory {

    val daoStorage = InmemoryHistoryHeightsDao()
    val dao: HistoryHeightsDao = daoStorage

    val citizenId = "3212343234123"
    val citizenId2 = "2716355425367"

    val height = Height(170.1F)
    val height2 = Height(169.0F)

    @Before
    fun setUp() {
        daoStorage.cleanAll()
    }

    @Test
    override fun save() {
        val save = dao.save(citizenId, height)

        save.first().time `should be equal to` height.time
        save.first().height `should be equal to` height.height
    }

    @Test
    override fun get() {
        dao.save(citizenId, height)
        val get = dao.get(citizenId)
        get.first().time `should be equal to` height.time
        get.first().height `should be equal to` height.height
    }

    @Test
    override fun saveMulti() {
        dao.save(citizenId, height)
        val save = dao.save(citizenId, height2)

        save.first().time `should equal` height2.time
        save.first().height `should be equal to` height2.height
        save.first().time `should be equal to` height2.time

        save.last().time `should equal` height.time
        save.last().height `should be equal to` height.height
        save.last().time `should be equal to` height.time
    }

    @Test
    override fun getMulti() {
        dao.save(citizenId, height)
        dao.save(citizenId, height2)

        val get = dao.get(citizenId)

        get.first().time `should equal` height2.time
        get.first().height `should be equal to` height2.height
        get.first().time `should be equal to` height2.time

        get.last().time `should equal` height.time
        get.last().height `should be equal to` height.height
        get.last().time `should be equal to` height.time
    }

    @Test
    override fun save2Citizen() {
        val save1 = dao.save(citizenId, height)
        val save2 = dao.save(citizenId2, height2)

        save1.first().time `should equal` height.time
        save1.first().height `should be equal to` height.height
        save1.first().time `should be equal to` height.time

        save2.first().time `should equal` height2.time
        save2.first().height `should be equal to` height2.height
        save2.first().time `should be equal to` height2.time
    }

    @Test
    override fun get2Citizen() {
        dao.save(citizenId, height)
        dao.save(citizenId2, height2)

        val get1 = dao.get(citizenId)
        val get2 = dao.get(citizenId2)

        get1.first().time `should equal` height.time
        get1.first().height `should be equal to` height.height
        get1.first().time `should be equal to` height.time

        get2.first().time `should equal` height2.time
        get2.first().height `should be equal to` height2.height
        get2.first().time `should be equal to` height2.time
    }
}
