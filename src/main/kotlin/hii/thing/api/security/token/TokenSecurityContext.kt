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

package hii.thing.api.security.token

import java.security.Principal
import javax.ws.rs.core.SecurityContext

class TokenSecurityContext(val token: String, private val scheme: String) : SecurityContext {

    // TODO ยังไม่ได้กำหนด TokenManager
    lateinit var tokenManager: TokenManager

    override fun isUserInRole(role: String): Boolean {
        return tokenManager.getUserRole(token).contains(role)
    }

    override fun getAuthenticationScheme(): String = "Bearer"

    override fun getUserPrincipal(): Principal = Principal { token }

    override fun isSecure() = "https" == scheme
}