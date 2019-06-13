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

package hii.thing.api.security

import hii.thing.api.getLogger
import hii.thing.api.security.token.TokenManager
import hii.thing.api.security.token.TokenSecurityContext
import javax.annotation.Priority
import javax.annotation.security.RolesAllowed
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.ext.Provider

@Priority(Priorities.AUTHENTICATION)
@Provider
class RoleTokenSecurity : ContainerRequestFilter {

    @Context
    lateinit var resourceInfo: ResourceInfo

    // TODO ยังไม่ได้กำหนด TokenManager
    lateinit var tokenManager: TokenManager

    override fun filter(requestContext: ContainerRequestContext) {
        val rolesAllowed: RolesAllowed? = resourceInfo.resourceMethod.getAnnotation(RolesAllowed::class.java)
            ?: resourceInfo.resourceClass.getAnnotation(RolesAllowed::class.java)

        val clientToken = requestContext.token ?: if (rolesAllowed != null)
            throw NotAuthorizedException("ไม่มีข้อมูลการยืนยันตัวตน", DummyChallenge())
        else
            return

        logger.debug("requestToken:$clientToken")
        check(tokenManager.isAccessToken(clientToken)) { "ไม่ใช่ Access token" }
        check(!tokenManager.isExpire(clientToken)) { "access token หมดอายุ" }

        val clientRole = tokenManager.getUserRole(clientToken)
        check(clientRole.containsSome(rolesAllowed!!.value.toList()))

        // Token ที่ดึงจาก securityContext จะได้เป็น access token
        requestContext.securityContext =
            TokenSecurityContext(clientToken, requestContext.uriInfo.baseUri.scheme, tokenManager)

        logger.info(
            "Name: ${tokenManager.getName(clientToken)} " +
                "Access:${requestContext.method} " +
                "Path:${requestContext.uriInfo.path}"
        )
    }

    /**
     * ดึง token ออกมาจาก header
     * @return Token
     */
    val ContainerRequestContext.token: String?
        get() {
            val authHeaders = headers[AUTHORIZATION_HEADER]
            if (authHeaders.isNullOrEmpty())
                return null
            val bearer = authHeaders.find { it.startsWith(BEARER_SCHEME) }
            return bearer?.replaceFirst(BEARER_SCHEME, "")?.trim()?.takeIf { it.isNotBlank() }
        }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_SCHEME = "Bearer "
        private val logger = getLogger()
    }

    private fun List<String>.containsSome(list: List<String>): Boolean {
        forEach {
            if (list.contains(it)) return true
        }
        return false
    }

    class DummyChallenge
}
