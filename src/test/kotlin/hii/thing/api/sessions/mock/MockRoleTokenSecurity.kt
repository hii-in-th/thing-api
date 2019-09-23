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

package hii.thing.api.sessions.mock

import hii.thing.api.security.RoleTokenSecurity
import javax.annotation.Priority
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.ext.Provider

@Priority(Priorities.AUTHENTICATION)
@Provider
class MockRoleTokenSecurity : ContainerRequestFilter {

    @Context
    lateinit var resourceInfo: ResourceInfo

    private val roleTokenSecurity = RoleTokenSecurity()

    init {
        roleTokenSecurity.tokenManager = MockTokenManager()
    }

    override fun filter(requestContext: ContainerRequestContext) {
        roleTokenSecurity.resourceInfo = resourceInfo
        roleTokenSecurity.filter(requestContext)
    }
}
