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

package hii.thing.api.vital.dao.lastresult

import hii.thing.api.PgSqlTestRule
import hii.thing.api.vital.BloodPressures
import hii.thing.api.vital.Result
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.jetbrains.exposed.sql.Table
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PgSqlLastResultDaoTest {
    @JvmField
    @Rule
    val pgsql = PgSqlTestRule(Table("tmp_last_result"))

    lateinit var dao: LastResultDao

    val laseResult = Result(18, 165F, 54F, BloodPressures(110F, 65F, 60F))
    val citizenId = "3212334483726"

    @Test
    fun set() {
        val result = dao.set(citizenId, laseResult)

        result.height!! `should be equal to` 165F
        result.age!! `should be equal to` 18
        result.weight!! `should be equal to` 54F
        result.bloodPressure!!.sys `should be equal to` 110F
        result.bloodPressure!!.dia `should be equal to` 65F
    }

    @Test
    fun setAgeHeightWeightNotSetBp() {
        val result = dao.set(citizenId, Result(18, 165F, 54F, null))

        result.height!! `should be equal to` 165F
        result.age!! `should be equal to` 18
        result.weight!! `should be equal to` 54F
        result.bloodPressure `should equal` null
        result.sex `should equal` null
    }

    @Test
    fun setSex() {
        val result = dao.set(citizenId, Result(null, null, null, null, "male"))

        result.height `should equal` null
        result.age `should equal` null
        result.weight `should equal` null
        result.bloodPressure `should equal` null
        result.sex!! `should be equal to` "male"
    }

    @Test
    fun setBpNotSetAgeHeightWeight() {
        val result = dao.set(citizenId, Result(null, null, null, BloodPressures(110F, 65F, 60F)))

        result.height `should equal` null
        result.age `should equal` null
        result.weight `should equal` null
        result.bloodPressure!!.sys `should be equal to` 110F
        result.bloodPressure!!.dia `should be equal to` 65F
        result.bloodPressure!!.pulse `should be equal to` 60F
    }

    @Test
    fun setWeight() {
        val result = dao.set(citizenId, Result(null, null, 56F, null))

        result.height `should equal` null
        result.age `should equal` null
        result.weight!! `should be equal to` 56F
        result.bloodPressure `should equal` null
    }

    @Test
    fun setAge() {
        val result = dao.set(citizenId, Result(56, null, null, null))

        result.height `should equal` null
        result.age!! `should be equal to` 56
        result.weight `should equal` null
        result.bloodPressure `should equal` null
    }

    @Test
    fun setHeight() {
        val result = dao.set(citizenId, Result(null, 156F, null, null))

        result.height!! `should be equal to` 156F
        result.age `should equal` null
        result.weight `should equal` null
        result.bloodPressure `should equal` null
    }

    @Test
    fun doubleSet() {
        dao.set(citizenId, Result(15, 190F, 60F, BloodPressures(113F, 67F, 60F)))
        val result = dao.set(citizenId, laseResult)

        result.height!! `should be equal to` 165F
        result.age!! `should be equal to` 18
        result.weight!! `should be equal to` 54F
        result.bloodPressure!!.sys `should be equal to` 110F
        result.bloodPressure!!.dia `should be equal to` 65F
        result.bloodPressure!!.pulse `should be equal to` 60F
    }

    @Test
    fun get() {
        dao.set(citizenId, laseResult)
        val result = dao.get(citizenId)

        result.height!! `should be equal to` 165F
        result.age!! `should be equal to` 18
        result.weight!! `should be equal to` 54F
        result.bloodPressure!!.sys `should be equal to` 110F
        result.bloodPressure!!.dia `should be equal to` 65F
    }

    @Test
    fun getByRefLink() {
        val set = dao.set(citizenId, laseResult)
        val result = dao.getBy(set.shareableLink!!)

        result.height!! `should be equal to` 165F
        result.age!! `should be equal to` 18
        result.weight!! `should be equal to` 54F
        result.bloodPressure!!.sys `should be equal to` 110F
        result.bloodPressure!!.dia `should be equal to` 65F
    }

    @Test(expected = Exception::class)
    fun getByEmptyRefLink() {
        dao.set(citizenId, laseResult)
        dao.getBy("dldkdkdkd")
    }

    @Test(expected = Exception::class)
    fun getEmpty() {
        dao.get(citizenId)
    }

    @Test
    fun remove() {
        dao.set(citizenId, laseResult)
        dao.remove(citizenId)

        runCatching { dao.get(citizenId) }.isFailure `should be equal to` true
    }

    @Test(expected = Exception::class)
    fun removeEmpty() {
        dao.remove(citizenId)
    }

    @Before
    fun setUp() {
        dao = PgSqlLastResultDao(pgsql.connection)
    }
}
