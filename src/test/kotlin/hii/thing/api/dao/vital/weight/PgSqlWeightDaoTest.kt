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

package hii.thing.api.dao.vital.weight

import hii.thing.api.vital.Weight
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres

class PgSqlWeightDaoTest {
    val pgsql = EmbeddedPostgres()
    lateinit var weightDao: WeightDao

    @Before
    fun setUp() {
        val url = pgsql.start()
        weightDao = PgSqlWeightDao(url, "postgres", "postgres")
    }

    @After
    fun tearDown() {
        pgsql.stop()
    }

    val sessionId = "max-199-991-888"
    val weight = Weight(68.2F)

    @Test
    fun save() {
        val save = weightDao.save(sessionId, weight)

        save.weight `should be equal to` weight.weight
        save.sessionId!! `should be equal to` sessionId
    }

    @Test(expected = Exception::class)
    fun saveDuplicateSession() {
        weightDao.save(sessionId, weight)
        weightDao.save(sessionId, Weight(45.9F))
    }

    @Test
    fun getBy() {
        weightDao.save(sessionId, weight)
        weightDao.save("test-001-99", Weight(50F))

        val get = weightDao.getBy(sessionId)

        get.weight `should be equal to` weight.weight
        get.sessionId!! `should be equal to` sessionId
    }
}
