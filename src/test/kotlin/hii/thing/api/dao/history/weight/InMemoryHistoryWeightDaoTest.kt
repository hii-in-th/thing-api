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

package hii.thing.api.dao.history.weight

import hii.thing.api.`should be equal to`
import hii.thing.api.dao.history.BaseTestHistory
import hii.thing.api.vital.Weight
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class InMemoryHistoryWeightDaoTest : BaseTestHistory {

    val daoStorage = InMemoryHistoryWeightDao()
    val dao: HistoryWeightDao = daoStorage

    val citizenId = "3212343234123"
    val citizenId2 = "2716355425367"

    val weight = Weight(68.2F)
    val weight2 = Weight(73.4F)

    @Before
    fun setUp() {
        daoStorage.cleanAll()
    }

    @Test
    override fun save() {
        val save = dao.save(citizenId, weight)

        save.first().time `should be equal to` weight.time
        save.first().weight `should be equal to` weight.weight
    }

    @Test
    override fun get() {
        dao.save(citizenId, weight)
        val get = dao.get(citizenId)

        get.first().time `should be equal to` weight.time
        get.first().weight `should be equal to` weight.weight
    }

    @Test
    override fun saveMulti() {
        dao.save(citizenId, weight)
        val save = dao.save(citizenId, weight2)

        save.first().time `should be equal to` weight.time
        save.first().weight `should be equal to` weight.weight
        save.last().time `should be equal to` weight2.time
        save.last().weight `should be equal to` weight2.weight
    }

    @Test
    override fun getMulti() {
        dao.save(citizenId, weight)
        dao.save(citizenId, weight2)
        val get = dao.get(citizenId)

        get.first().time `should be equal to` weight.time
        get.first().weight `should be equal to` weight.weight
        get.last().time `should be equal to` weight2.time
        get.last().weight `should be equal to` weight2.weight
    }

    @Test
    override fun save2Citizen() {
        val save = dao.save(citizenId, weight)
        val save2 = dao.save(citizenId2, weight2)

        save.first().time `should be equal to` weight.time
        save.first().weight `should be equal to` weight.weight
        save2.first().time `should be equal to` weight2.time
        save2.first().weight `should be equal to` weight2.weight
    }

    @Test
    override fun get2Citizen() {
        dao.save(citizenId, weight)
        dao.save(citizenId2, weight2)
        val get = dao.get(citizenId)
        val get2 = dao.get(citizenId2)

        get.first().time `should be equal to` weight.time
        get.first().weight `should be equal to` weight.weight
        get2.first().time `should be equal to` weight2.time
        get2.first().weight `should be equal to` weight2.weight
    }
}
