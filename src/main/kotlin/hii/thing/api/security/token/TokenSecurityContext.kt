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

import hii.thing.api.security.token.jwt.JwtThingPrincipal
import java.security.Principal
import javax.ws.rs.core.SecurityContext

class TokenSecurityContext(
    val token: String,
    val tokenManager: TokenManager
) :
    SecurityContext {

    override fun isUserInRole(role: String): Boolean {
        return if (token.isEmpty())
            role == "visitor"
        else
            tokenManager.getUserRole(token).contains(role)
    }

    override fun getAuthenticationScheme(): String = "Bearer"

    override fun getUserPrincipal(): Principal {
        return if (token.isEmpty())
            Principal { "visitor" }
        else {
            val run = kotlin.runCatching { JwtThingPrincipal(token) }
            if (run.isSuccess)
                run.getOrNull()!!
            else
                Principal { "visitor" }
        }
    }

    override fun isSecure() = true
}
