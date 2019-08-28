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

package hii.thing.api.dao.history.bp

import hii.thing.api.dao.history.BaseTestHistory
import hii.thing.api.vital.BloodPressures
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test

class InMemoryHistoryBloodPressuresDaoTest : BaseTestHistory {
    val daoStorage = InMemoryHistoryBloodPressuresDao()
    val dao: HistoryBloodPressuresDao = daoStorage

    val citizenId = "3212343234123"
    val citizenId2 = "2716355425367"

    val bp = BloodPressures(
        123.0F,
        140.6F
    )
    val bp2 = BloodPressures(
        110.0F,
        60.6F
    )

    @Test
    override fun save() {
        val save = dao.save(citizenId, bp)

        save.first().dia `should be equal to` bp.dia
        save.first().sys `should be equal to` bp.sys
        save.first().time `should equal` bp.time
    }

    @Test
    override fun get() {
        dao.save(citizenId, bp)
        val get = dao.get(citizenId)

        get.first().dia `should be equal to` bp.dia
        get.first().sys `should be equal to` bp.sys
        get.first().time `should equal` bp.time
    }

    @Test
    override fun saveMulti() {
        dao.save(citizenId, bp)
        val save = dao.save(citizenId, bp2)

        save.first().dia `should be equal to` bp.dia
        save.first().sys `should be equal to` bp.sys
        save.first().time `should equal` bp.time
        save.last().dia `should be equal to` bp2.dia
        save.last().sys `should be equal to` bp2.sys
        save.last().time `should equal` bp2.time
    }

    @Test
    override fun getMulti() {
        dao.save(citizenId, bp)
        dao.save(citizenId, bp2)

        val get = dao.get(citizenId)

        get.first().dia `should be equal to` bp.dia
        get.first().sys `should be equal to` bp.sys
        get.first().time `should equal` bp.time
        get.last().dia `should be equal to` bp2.dia
        get.last().sys `should be equal to` bp2.sys
        get.last().time `should equal` bp2.time
    }

    @Test
    override fun save2Citizen() {
        val save = dao.save(citizenId, bp)
        val save2 = dao.save(citizenId2, bp2)

        save.first().dia `should be equal to` bp.dia
        save.first().sys `should be equal to` bp.sys
        save.first().time `should equal` bp.time
        save2.first().dia `should be equal to` bp2.dia
        save2.first().sys `should be equal to` bp2.sys
        save2.first().time `should equal` bp2.time
    }

    @Test
    override fun get2Citizen() {
        dao.save(citizenId, bp)
        dao.save(citizenId2, bp2)

        val get = dao.get(citizenId)
        val get2 = dao.get(citizenId2)

        get.first().dia `should be equal to` bp.dia
        get.first().sys `should be equal to` bp.sys
        get.first().time `should equal` bp.time
        get2.first().dia `should be equal to` bp2.dia
        get2.first().sys `should be equal to` bp2.sys
        get2.first().time `should equal` bp2.time
    }

    @Before
    fun setUp() {
        daoStorage.cleanAll()
    }
}
