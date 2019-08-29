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

package hii.thing.api.persons

import hii.thing.api.config.GsonJerseyProvider
import hii.thing.api.dao.registerstore.InMemoryRegisterStoreDao
import hii.thing.api.dao.vital.height.InMemoryHeightsDao
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.vital.Height
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class HeightsHistoryTest {
    private val heightsDao = InMemoryHeightsDao()
    private val registerStoreDao = InMemoryRegisterStoreDao()
    private val heightsHistory = History(registerStoreDao) {
        val item = heightsDao.getBy(it)
        item to item.time
    }

    val citizenId = "1234322340985"
    val citizenId2 = "4830288374203"

    lateinit var height: Height
    lateinit var height2: Height
    lateinit var height3: Height

    companion object {
        var everInit = false
    }

    @Before
    fun setUp() {
        runBlocking {
            height = Height(170.1F)
            delay(10)
            height2 = Height(169.0F)
            delay(10)
            height3 = Height(140.0F)
        }
        if (!everInit) {
            initSetup()
            everInit = true
        }
    }

    @Test
    fun getFirstPerson() {
        val get = heightsHistory.get(citizenId)
        println(GsonJerseyProvider.hiiGson.toJson(get))

        get.size `should be equal to` 3
        get[0].height `should be equal to` height3.height
        get[1].height `should be equal to` height2.height
        get[2].height `should be equal to` height.height
    }

    @Test
    fun getSecoundPerson() {
        val get = heightsHistory.get(citizenId2)
        println(GsonJerseyProvider.hiiGson.toJson(get))

        get.size `should be equal to` 3
        get[0].height `should be equal to` height3.height
        get[1].height `should be equal to` height2.height
        get[2].height `should be equal to` height.height
    }

    private fun initSetup() {
        val detail = CreateSessionDetail(
            "max-199-991",
            citizenId,
            CreateSessionDetail.InputType.CARD,
            "1970-10-13",
            "thanachai",
            CreateSessionDetail.Sex.MALE
        )
        val detail2 = CreateSessionDetail(
            "aaa/000",
            citizenId2,
            CreateSessionDetail.InputType.TYPING,
            "1970-10-16",
            "nstda",
            CreateSessionDetail.Sex.FEMALE
        )

        val sessionId = "123-324-234-435"
        val sessionId2 = "2324-234-14234-45"
        val sessionId3 = "2324-234-14234-23"

        registerStoreDao.register(sessionId, detail)
        registerStoreDao.register(sessionId2, detail)
        registerStoreDao.register(sessionId3, detail)
        heightsDao.save(sessionId, height)
        heightsDao.save(sessionId2, height2)
        heightsDao.save(sessionId3, height3)

        val sessionId4 = "123-324-43435-435"
        val sessionId5 = "2324-234-238-45"
        val sessionId6 = "2324-234-654-23"

        registerStoreDao.register(sessionId4, detail2)
        registerStoreDao.register(sessionId5, detail2)
        registerStoreDao.register(sessionId6, detail2)
        heightsDao.save(sessionId4, height3)
        heightsDao.save(sessionId5, height2)
        heightsDao.save(sessionId6, height)
    }
}
