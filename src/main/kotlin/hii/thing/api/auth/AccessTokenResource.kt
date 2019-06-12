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

package hii.thing.api.auth

import hii.thing.api.getLogger
import hii.thing.api.ignore
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType

@Path("/auth/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AccessTokenResource(val manager: TokenManager) {

    @Context
    lateinit var req: HttpServletRequest

    @Context
    lateinit var headers: HttpHeaders

    @POST
    fun createAccessToken(): AccessToken {
        logger.info("Create access token by ip:${ignore { req.remoteAddr }}")

        val bearer = headers.getHeaderString("Authorization")?.takeIf { it.isNotBlank() }
        logger.debug("Bearer $bearer")
        val baseKey = bearer?.replaceFirst("Bearer ", "")?.trim()?.takeIf { it.isNotBlank() }
        logger.debug("Base token $baseKey")

        check(bearer != null) { "ไม่พบส่วนของ Authorization ใน http header" }
        check(bearer.startsWith("Bearer ")) { "รูปแบบ Authorization ใน http header ไม่ถูกต้อง" }
        check(baseKey != null) { "ไม่พบ Base token ใน Authorization" }

        return manager.create(baseKey)
    }

    companion object {
        val logger = getLogger()
    }
}
