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

import hii.thing.api.sessions.jwt.JwtSessionsManager
import javax.annotation.security.RolesAllowed
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext

@Path("/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

class SessionsResource(
    private val sessionsManager: SessionsManager = JwtSessionsManager()
) {

    @Context
    lateinit var context: SecurityContext

    @POST
    @RolesAllowed("MACHINE")
    fun newSessions(detail: CreateSessionDetail): Session {
        val accessToken = context.userPrincipal.name!!
        // session create
        return Session(
            sessionsManager.anonymousCreate(accessToken, detail.deviceId)
            // TODO รอสร้างตัวดึงข้อมูลที่วัดไปล่าสุด detail.citizenId
        )
    }
}
