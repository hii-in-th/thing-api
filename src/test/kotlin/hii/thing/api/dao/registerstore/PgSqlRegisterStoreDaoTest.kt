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

package hii.thing.api.dao.registerstore

import hii.thing.api.PgSqlTestRule
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.CreateSessionDetail.InputType.CARD
import hii.thing.api.sessions.CreateSessionDetail.InputType.TYPING
import hii.thing.api.sessions.CreateSessionDetail.Sex.FEMALE
import hii.thing.api.sessions.CreateSessionDetail.Sex.MALE
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.jetbrains.exposed.sql.Table
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PgSqlRegisterStoreDaoTest {
    @JvmField
    @Rule
    val pgsql = PgSqlTestRule(Table("register"))
    lateinit var registerStoreDao: RegisterStoreDao

    @Before
    fun setUp() {
        registerStoreDao = PgSqlRegisterStoreDao(pgsql.connection)
    }

    val sessionId = "12384-sdf-b-a-2-321-32-4"
    val createSessionDetail = CreateSessionDetail(
        "max-199-991",
        "1234122345634",
        CARD,
        "1970-10-13",
        "thanachai",
        MALE
    )
    val anonymous = CreateSessionDetail(
        "aaa/000",
        "3342",
        TYPING,
        "1970-10-16",
        "nstda",
        FEMALE
    )

    @Test
    fun register() {
        val reg = registerStoreDao.register(sessionId, createSessionDetail)

        reg.deviceId `should be equal to` createSessionDetail.deviceId
        reg.citizenId!! `should be equal to` createSessionDetail.citizenId!!
        reg.citizenIdInput!! `should equal` createSessionDetail.citizenIdInput!!
        reg.birthDate!! `should be equal to` createSessionDetail.birthDate!!
        reg.name!! `should be equal to` createSessionDetail.name!!
        reg.sex!! `should equal` createSessionDetail.sex!!
    }

    @Test(expected = Exception::class)
    fun registerDuplicate() {
        registerStoreDao.register(sessionId, createSessionDetail)
        registerStoreDao.register(sessionId, anonymous)
    }

    @Test
    fun update() {
        registerStoreDao.register(sessionId, createSessionDetail)
        val update = registerStoreDao.update(sessionId, anonymous)

        update.deviceId `should be equal to` anonymous.deviceId
        update.citizenId!! `should be equal to` anonymous.citizenId!!
        update.citizenIdInput!! `should equal` anonymous.citizenIdInput!!
        update.birthDate!! `should be equal to` anonymous.birthDate!!
        update.name!! `should be equal to` anonymous.name!!
        update.sex!! `should equal` anonymous.sex!!
    }

    @Test(expected = Exception::class)
    fun updateEmptySession() {
        registerStoreDao.update(sessionId, anonymous)
    }

    @Test
    fun getBy() {
        registerStoreDao.register(sessionId, createSessionDetail)
        registerStoreDao.register("xam-sds", anonymous)

        val getReg = registerStoreDao.getBy(createSessionDetail.citizenId!!)

        getReg.size `should be equal to` 1
        getReg[sessionId]!!.deviceId `should be equal to` createSessionDetail.deviceId
    }

    @Test
    fun get() {
        registerStoreDao.register(sessionId, createSessionDetail)
        registerStoreDao.register("xam-sds", anonymous)

        val getReg = registerStoreDao.get(sessionId)
        getReg.deviceId `should be equal to` createSessionDetail.deviceId
    }
}
