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

package hii.thing.api.vital

import hii.thing.api.config.cache.Cache
import hii.thing.api.dao.getDao
import hii.thing.api.dao.lastresult.GenUrl
import hii.thing.api.dao.lastresult.LastResultDao
import hii.thing.api.dao.refResultLinkLength
import hii.thing.api.dao.registerstore.toJavaTime
import hii.thing.api.dao.vital.bp.BloodPressuresDao
import hii.thing.api.dao.vital.height.HeightsDao
import hii.thing.api.dao.vital.weight.WeightDao
import hii.thing.api.getLogger
import hii.thing.api.ignore
import hii.thing.api.security.token.ThingPrincipal
import hii.thing.api.sessions.SessionsManager
import hii.thing.api.sessions.jwt.JwtSessionsManager
import hii.thing.api.vital.link.JwtLink
import hii.thing.api.vital.link.Link
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.annotation.security.RolesAllowed
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

class VitalResource(
    val pbDao: BloodPressuresDao = getDao(),
    val heightsDao: HeightsDao = getDao(),
    val weightDao: WeightDao = getDao(),
    val sessionsManager: SessionsManager = JwtSessionsManager(),
    private val lastResultDao: LastResultDao = getDao(),
    val link: Link = JwtLink()
) {

    @Context
    lateinit var context: SecurityContext

    @POST
    @Path("/blood-pressures")
    @RolesAllowed("kiosk")
    fun bpMeasure(map: Map<String, String>): BloodPressures {
        val pulse = map.getValue("pulse").toFloatOrNull()
        val sys = map.getValue("sys").toFloatOrNull()
        val dia = map.getValue("dia").toFloatOrNull()
        require(pulse != null) { "ต้องมีข้อมูล pulse" }
        require(sys != null) { "ต้องมีข้อมูล sys" }
        require(dia != null) { "ต้องมีข้อมูล dia" }

        val bp = BloodPressures(sys, dia, pulse)
        val userPrincipal = (context.userPrincipal as ThingPrincipal)
        val session = sessionsManager.getBy(userPrincipal.accessToken)
        bp.sessionId = session

        val save = pbDao.save(session, bp)
        logger.info {
            "MeasureBP\t" +
                "sys:${save.sys}\t" +
                "dia:${save.dia}\t" +
                "BPLevel:${save.level}\t" +
                "age:${sessionsManager.getDetail(session).age}"
        }
        return save
    }

    @POST
    @Path("/heights")
    @RolesAllowed("kiosk")
    fun heightsMeasure(map: Map<String, String>): Height {
        val height = Height(map.getValue("height").toFloat())
        val userPrincipal = (context.userPrincipal as ThingPrincipal)
        val session = sessionsManager.getBy(userPrincipal.accessToken)
        height.sessionId = session

        val save = heightsDao.save(session, height)
        val weight = ignore { weightDao.getBy(session) }
        logger.info {
            "MeasureHeight\t" +
                "height:${save.height}\t" +
                "weight:${weight?.weight}\t" +
                "age:${sessionsManager.getDetail(session).age}"
        }
        return save
    }

    @POST
    @Path("/weights")
    @RolesAllowed("kiosk")
    fun weightMeasure(map: Map<String, String>): Weight {
        val weight = Weight(map.getValue("weight").toFloat())
        val userPrincipal = (context.userPrincipal as ThingPrincipal)
        val session = sessionsManager.getBy(userPrincipal.accessToken)
        weight.sessionId = session

        val save = weightDao.save(session, weight)
        val height = ignore { heightsDao.getBy(session) }
        logger.info {
            "MeasureWeight\t" +
                "height:${height?.height}\t" +
                "weight:${weight.weight}\t" +
                "age:${sessionsManager.getDetail(session).age}"
        }
        return save
    }

    @GET
    @Path("/result")
    @RolesAllowed("kiosk")
    fun getResult(): Result {
        val userPrincipal = (context.userPrincipal as ThingPrincipal)
        return result(userPrincipal)
    }

    @GET
    @Path("/lresult")
    @RolesAllowed("kiosk", "report")
    @Cache(maxAge = 1000)
    fun lGetResult(): Result {
        val userPrincipal = (context.userPrincipal as ThingPrincipal)
        return result(userPrincipal)
    }

    fun result(userPrincipal: ThingPrincipal): Result {
        val replayId = link.getRefId(userPrincipal.accessToken)

        return if (!replayId.isNullOrEmpty()) { //  ตรวจสอบว่าเป็น Link แบบดูย้อนหลังหรือไม่
            logger.debug { "Replay result $replayId" }
            val lastResult = lastResultDao.getBy(replayId)
            lastResult.shareableLink = null //  ดึงผลเก่าออกมา
            lastResult
        } else {
            createNewResult(userPrincipal)
        }
    }

    private fun createNewResult(userPrincipal: ThingPrincipal): Result {
        val session = sessionsManager.getBy(userPrincipal.accessToken)
        val height = ignore { heightsDao.getBy(session) }
        val weight = ignore { weightDao.getBy(session) }
        val bp = ignore { pbDao.getBy(session) }

        val userDetail = kotlin.runCatching { sessionsManager.getDetail(session) }.getOrNull()
        val age: Int? = userDetail?.birthDate?.let { calAge(it.toJavaTime()) }

        val result = Result(age, height?.height, weight?.weight, bp, userDetail?.sex?.toString())

        userDetail?.citizenId?.let {
            val replayId = GenUrl(refResultLinkLength).nextSecret()
            var replayLink: String? = null
            runBlocking {
                launch { lastResultDao.set(it, result, replayId) }
                launch { replayLink = "https://thing-api.hii.in.th/v1/lresult?token=${link.create(replayId)}" }
            }
            result.shareableLink = replayLink
        }
        return result
    }

    companion object {
        val logger by lazy { getLogger() }
    }
}
