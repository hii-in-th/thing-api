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

import hii.thing.api.config.cache.Cache
import hii.thing.api.dao.getDao
import hii.thing.api.dao.registerstore.RegisterStoreDao
import hii.thing.api.dao.vital.bp.BloodPressuresDao
import hii.thing.api.dao.vital.height.HeightsDao
import hii.thing.api.dao.vital.weight.WeightDao
import hii.thing.api.security.token.ThingPrincipal
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.SessionsManager
import hii.thing.api.sessions.jwt.JwtSessionsManager
import hii.thing.api.vital.BloodPressures
import hii.thing.api.vital.Height
import hii.thing.api.vital.Weight
import javax.annotation.security.RolesAllowed
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class HistoryResource(
    private val sessionsManager: SessionsManager = JwtSessionsManager(),
    private val registerStoreDao: RegisterStoreDao = getDao()
) {
    @Context
    lateinit var context: SecurityContext

    private val heightsDao by lazy { getDao<HeightsDao>() }
    private val weightDao by lazy { getDao<WeightDao>() }
    private val bpDao by lazy { getDao<BloodPressuresDao>() }

    private val heightHistory by lazy { History { with(heightsDao.getBy(it)) { this to this.time } } }
    private val weightHistory by lazy { History { with(weightDao.getBy(it)) { this to this.time } } }
    private val bpHistory by lazy { History { with(bpDao.getBy(it)) { this to this.time } } }

    @GET
    @RolesAllowed("kiosk")
    @Cache(maxAge = 5)
    @Path("/{citizenId:(\\d+)}/height")
    fun heights(@PathParam("citizenId") citizenId: String): List<Height> {
        val detail = getRegisterDetail()
        require(detail.citizenId == citizenId) { "ไม่สามารถดูข้อมูล นอกเหนือจากข้อมูลตัวเอง" }
        return heightHistory.get(citizenId)
    }

    @GET
    @RolesAllowed("kiosk")
    @Cache(maxAge = 5)
    @Path("/{citizenId:(\\d+)}/weight")
    fun weight(@PathParam("citizenId") citizenId: String): List<Weight> {
        val detail = getRegisterDetail()
        require(detail.citizenId == citizenId) { "ไม่สามารถดูข้อมูล นอกเหนือจากข้อมูลตัวเอง" }
        return weightHistory.get(citizenId)
    }

    @GET
    @RolesAllowed("kiosk")
    @Cache(maxAge = 5)
    @Path("/{citizenId:(\\d+)}/bloodpressure")
    fun bloodPressures(@PathParam("citizenId") citizenId: String): List<BloodPressures> {
        val detail = getRegisterDetail()
        require(detail.citizenId == citizenId) { "ไม่สามารถดูข้อมูล นอกเหนือจากข้อมูลตัวเอง" }
        return bpHistory.get(citizenId)
    }

    private fun getRegisterDetail(): CreateSessionDetail {
        val userPrincipal = (context.userPrincipal as ThingPrincipal)
        val sessionId = sessionsManager.getBy(userPrincipal.accessToken)
        val detail = registerStoreDao.get(sessionId)
        return detail
    }
}
