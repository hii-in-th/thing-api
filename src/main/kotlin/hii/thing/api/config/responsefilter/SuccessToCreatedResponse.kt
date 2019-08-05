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

package hii.thing.api.config.responsefilter

import javax.annotation.Priority
import javax.ws.rs.HttpMethod
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.ext.Provider

/**
 * หาก client POST เข้ามาแล้วสำเร็จ ให้ส่งกลับเป็น 201
 */
@Suppress("UNUSED_VARIABLE")
@Priority(Priorities.HEADER_DECORATOR)
@Provider
class SuccessToCreatedResponse : ContainerResponseFilter {
    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext
    ) {
        val isPost = requestContext.method == HttpMethod.POST

        if (isPost && responseContext.status == 200) {
            responseContext.status = 201
        }
    }
}
