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

package hii.thing.api.sessions

import hii.thing.api.dao.getDao
import hii.thing.api.dao.lastresult.LastResultDao
import hii.thing.api.getLogger
import hii.thing.api.hashText
import hii.thing.api.ignore
import hii.thing.api.security.token.ThingPrincipal
import hii.thing.api.sessions.CreateSessionDetail.InputType.CARD
import hii.thing.api.sessions.CreateSessionDetail.InputType.UNDEFINED
import hii.thing.api.sessions.jwt.JwtSessionsManager
import hii.thing.api.vital.Result
import hii.thing.api.vital.VitalResource
import javax.annotation.security.RolesAllowed
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext

@Path("/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

class SessionsResource(
    private val sessionsManager: SessionsManager = JwtSessionsManager(),
    private val lastResultDao: LastResultDao = getDao()
) {

    @Context
    lateinit var context: SecurityContext

    @POST
    @RolesAllowed("kiosk")
    fun newSessions(detail: CreateSessionDetail): Session {
        val userPrincipal = context.userPrincipal as ThingPrincipal
        val newDetail = // Repeat real deviceId
            CreateSessionDetail(
                userPrincipal.deviceName,
                detail.citizenId.takeIf { !it.isNullOrBlank() },
                detail.citizenIdInput ?: UNDEFINED,
                detail.birthDate,
                detail.name,
                detail.sex
            )
        val session = {
            val lastResult =
                if (newDetail.citizenId.isNullOrBlank()) null else ignore { lastResultDao.get(newDetail.citizenId) }
            val sessionId = sessionsManager.create(userPrincipal.accessToken, newDetail)
            when (newDetail.citizenIdInput) { // ตรวจสอบรูปแบบการ Input ข้อมูลบัตรประชาชน
                CARD -> {
                    Session(sessionId, lastResult)
                }
                else -> {
                    Session(
                        sessionId, Result(null, lastResult?.height, lastResult?.weight, null)
                    )
                }
            }
        }.invoke()

        logger.info {
            "UserUse\t" +
                "Id:${session.sessionId}\t" +
                "Name:${userPrincipal.deviceName}\t" +
                "InputType:${newDetail.citizenIdInput}\t" +
                "Sex:${newDetail.sex?.toString() ?: "Unknown"}\t" +
                "Citizen:${
                if (!newDetail.citizenId.isNullOrBlank())
                    newDetail.citizenId.hashText()
                else
                    "anonymous"
                }"
        }
        session.subject?.shareableLink = null
        return session
    }

    @PUT
    @RolesAllowed("kiosk")
    fun updateSessions(detail: CreateSessionDetail): Session {
        val userPrincipal = context.userPrincipal as ThingPrincipal
        val session = sessionsManager.getBy(userPrincipal.accessToken)
        require(sessionsManager.getDetail(session).citizenId.isNullOrEmpty()) { "มีการใส่ข้อมูลส่วนตัวไปแล้ว ไม่สามารถใส่ซ้ำได้" }
        sessionsManager.updateCreate(
            userPrincipal.accessToken, CreateSessionDetail(
                userPrincipal.deviceName,
                detail.citizenId,
                detail.citizenIdInput,
                detail.birthDate,
                detail.name,
                detail.sex
            )
        )

        return Session(session, VitalResource().result(userPrincipal))
    }

    companion object {
        private val logger by lazy { getLogger() }
    }
}
