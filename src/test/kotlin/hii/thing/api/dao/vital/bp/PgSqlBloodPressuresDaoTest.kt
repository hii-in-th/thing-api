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

package hii.thing.api.dao.vital.bp

import hii.thing.api.vital.BloodPressures
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres

class PgSqlBloodPressuresDaoTest {
    val pgsql = EmbeddedPostgres()
    lateinit var pbDao: PgSqlBloodPressuresDao

    @Before
    fun setUp() {
        val url = pgsql.start()
        pbDao = PgSqlBloodPressuresDao(url, "postgres", "postgres")
    }

    @After
    fun tearDown() {
        pgsql.stop()
    }

    val sessionId = "13432-sdfdsf-dsf-344435"
    val pb = BloodPressures(
        123.0F,
        140.6F
    )
    val pb2 = BloodPressures(
        110.0F,
        60.6F
    )

    @Test
    fun save() {
        val save = pbDao.save(sessionId, pb)

        save.sessionId!! `should be equal to` sessionId
        save.dia `should be equal to` pb.dia
        save.sys `should be equal to` pb.sys
        println(save.time)
    }

    @Test(expected = Exception::class)
    fun saveDuplicate() {
        pbDao.save(sessionId, pb)
        pbDao.save(sessionId, pb2)
    }

    @Test
    fun getBy() {
        pbDao.save(sessionId, pb)

        val get = pbDao.getBy(sessionId)

        get.sessionId!! `should be equal to` sessionId
        get.dia `should be equal to` pb.dia
        get.sys `should be equal to` pb.sys
        println(get.time)
    }

    @Test(expected = Exception::class)
    fun getEmpty() {
        pbDao.getBy(sessionId)
    }
}
