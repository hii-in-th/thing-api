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

package hii.thing.api.dao.lastresult

import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Test

class InMemoryLastResultDaoTest {

    val memory = InMemoryLastResultDao()
    val dao: LastResultDao = memory

    val laseResult = mapOf("Height" to "1570", "Me" to "i")
    val citizenId = "3212334483726"

    @Test
    fun set() {
        val result = dao.set(citizenId, laseResult)

        result["Height"]!! `should be equal to` "1570"
        result["Me"]!! `should be equal to` "i"
    }

    @Test
    fun doubleSet() {
        dao.set(citizenId, laseResult)
        val result = dao.set(citizenId, mapOf("Height" to "99.9"))

        result["Height"]!! `should be equal to` "99.9"
    }

    @Test
    fun append() {
        dao.set(citizenId, laseResult)
        val result = dao.append(citizenId, mapOf("age" to "38"))

        result["Height"]!! `should be equal to` "1570"
        result["Me"]!! `should be equal to` "i"
        result["age"]!! `should be equal to` "38"
    }

    @Test
    fun appendEmpty() {
        dao.append(citizenId, laseResult)
        val result = dao.append(citizenId, mapOf("age" to "38"))

        result["Height"]!! `should be equal to` "1570"
        result["Me"]!! `should be equal to` "i"
        result["age"]!! `should be equal to` "38"
    }

    @Test
    fun appendOverlap() {
        dao.append(citizenId, laseResult)
        val result = dao.append(citizenId, mapOf("age" to "38", "Height" to "99.9"))

        result["Height"]!! `should be equal to` "99.9"
        result["Me"]!! `should be equal to` "i"
        result["age"]!! `should be equal to` "38"
    }

    @Test
    fun get() {
        dao.set(citizenId, laseResult)

        val result = dao.get(citizenId)

        result["Height"]!! `should be equal to` "1570"
        result["Me"]!! `should be equal to` "i"
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

    @After
    fun tearDown() {
        memory.cleanAll()
    }
}
