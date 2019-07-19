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
import hii.thing.api.security.token.jwt.JwtTokenManager
import javax.annotation.Priority
import javax.annotation.security.RolesAllowed
import javax.ws.rs.NotFoundException
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.ext.Provider

/**
 * ทุกครั้งที่มีการเรียกเข้ามาที่ api จะต้องผ่านตัวกรองที่นี่
 */
@Priority(Priorities.AUTHENTICATION)
@Provider
class RoleTokenSecurity : ContainerRequestFilter {

    @Context
    lateinit var resourceInfo: ResourceInfo

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_SCHEME = "Bearer "
        private val logger = RoleTokenSecurity.getLogger()
    }

    var tokenManager: TokenManager = JwtTokenManager()

    override fun filter(requestContext: ContainerRequestContext) {
        val rolesAllowed: RolesAllowed? = resourceInfo.resourceMethod.getAnnotation(RolesAllowed::class.java)
            ?: resourceInfo.resourceClass.getAnnotation(RolesAllowed::class.java)

        val clientToken = requestContext.token ?: if (rolesAllowed != null)
            throw NotFoundException("")
        else {
            requestContext.securityContext = TokenSecurityContext("", tokenManager)
            return
        }

        logger.debug("requestToken:$clientToken")
        if (rolesAllowed != null) {
            // require(requestContext.isCsrf) { "CSRF" }
            requireJwt(tokenManager.verify(clientToken/*, requestContext.uriInfo.path*/)) { "Verify token ผิดพลาด" }

            val clientRole = tokenManager.getUserRole(clientToken)
            require(clientRole.containsSome(rolesAllowed.value.toList())) { "ไม่มีสิทธิในการใช้งาน" }
        }
        // Token ที่ดึงจาก securityContext จะได้เป็น access token
        requestContext.securityContext =
            TokenSecurityContext(clientToken, tokenManager)
        logger.info(
            "Name: ${if (rolesAllowed != null) tokenManager.getName(clientToken) else clientToken} " +
                "Access:${requestContext.method} " +
                "Path:${requestContext.uriInfo.path}"
        )
    }

    /**
     * ดึง token ออกมาจาก header
     * หรือ ถ้าไม่มีจะดึงมาจาก query?token=
     * @return Token
     */
    val ContainerRequestContext.token: String?
        get() {
            val authHeaders = headers[AUTHORIZATION_HEADER]
            if (authHeaders.isNullOrEmpty()) {
                val token = this.uriInfo.queryParameters["token"]?.firstOrNull() // ดึง token จาก query
                return if (token.isNullOrEmpty())
                    null
                else
                    token
            }
            val bearer = authHeaders.find { it.startsWith(BEARER_SCHEME) }
            return bearer?.replaceFirst(BEARER_SCHEME, "")?.trim()?.takeIf { it.isNotBlank() }
        }

    /**
     * ตรวจสอบ Csrf
     */
    val ContainerRequestContext.isCsrf: Boolean
        get() {
            val hiiXreq = headers["X-Requested-By"]
            val token = this.token
            if (hiiXreq.isNullOrEmpty() || token.isNullOrEmpty())
                return false
            val name = tokenManager.getName(token)
            return !hiiXreq.find { it == name }.isNullOrEmpty()
        }

    private fun List<String>.containsSome(list: List<String>): Boolean {
        forEach {
            if (list.contains(it)) return true
        }
        return false
    }

    class DummyChallenge
}
