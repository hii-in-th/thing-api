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

package hii.thing.api.sessions.dao.recordsession

import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.CreateSessionDetail.InputType.CARD
import hii.thing.api.sessions.CreateSessionDetail.InputType.TYPING
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test

class InMemoryRecordSessionDaoTest {

    val inmemory = InMemoryRecordSessionDao()
    val recordSessionDao: RecordSessionDao = InMemoryRecordSessionDao()

    val sessionId = "12384-sdf-b-a-2-321-32-4"
    val createSessionDetail = CreateSessionDetail(
        "max-199-991",
        "1234122345634",
        CARD,
        "1970-10-13",
        "thanachai",
        CreateSessionDetail.Sex.MALE,
        "192.168.1.1"
    )
    val anonymous = CreateSessionDetail(
        "aaa/000",
        "3342",
        TYPING,
        "1970-10-16",
        "nstda",
        CreateSessionDetail.Sex.FEMALE,
        "192.168.1.2"
    )

    @Test
    fun register() {
        val reg = recordSessionDao.register(sessionId, createSessionDetail)

        reg.deviceId `should be equal to` createSessionDetail.deviceId
        reg.citizenId!! `should be equal to` createSessionDetail.citizenId!!
        reg.citizenIdInput!! `should equal` createSessionDetail.citizenIdInput!!
        reg.birthDate!! `should be equal to` createSessionDetail.birthDate!!
        reg.name!! `should be equal to` createSessionDetail.name!!
        reg.sex!! `should equal` createSessionDetail.sex!!
        reg.ipAddress!! `should equal` createSessionDetail.ipAddress!!
    }

    @Test(expected = Exception::class)
    fun registerDuplicate() {
        recordSessionDao.register(sessionId, createSessionDetail)
        recordSessionDao.register(sessionId, anonymous)
    }

    @Test
    fun update() {
        recordSessionDao.register(sessionId, createSessionDetail)
        val update = recordSessionDao.update(sessionId, anonymous)

        update.deviceId `should be equal to` anonymous.deviceId
        update.citizenId!! `should be equal to` anonymous.citizenId!!
        update.citizenIdInput!! `should equal` anonymous.citizenIdInput!!
        update.birthDate!! `should be equal to` anonymous.birthDate!!
        update.name!! `should be equal to` anonymous.name!!
        update.sex!! `should equal` anonymous.sex!!
        update.ipAddress!! `should equal` anonymous.ipAddress!!
    }

    @Test(expected = Exception::class)
    fun updateEmptySession() {
        recordSessionDao.update(sessionId, anonymous)
    }

    @Test
    fun getBy() {
        recordSessionDao.register(sessionId, createSessionDetail)
        recordSessionDao.register("xam-sds", anonymous)

        val getReg = recordSessionDao.getBy(createSessionDetail.citizenId!!)

        getReg.size `should be equal to` 1
        getReg[sessionId]!!.deviceId `should be equal to` createSessionDetail.deviceId
    }

    @Test
    fun get() {
        recordSessionDao.register(sessionId, createSessionDetail)
        recordSessionDao.register("xam-sds", anonymous)

        val getReg = recordSessionDao.get(sessionId)
        getReg.deviceId `should be equal to` createSessionDetail.deviceId
    }

    @Before
    fun setUp() {
        inmemory.cleanAll()
    }
}
