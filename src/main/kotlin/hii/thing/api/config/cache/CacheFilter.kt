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

package hii.thing.api.config.cache

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.CacheControl
import javax.ws.rs.core.Context
import javax.ws.rs.ext.Provider

@Provider
class CacheFilter : ContainerResponseFilter {
    @Context
    private lateinit var resourceInfo: ResourceInfo

    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        if (!isCacheable(responseContext)) {
            responseContext.headers.add("Cache-Control", "max-age=0")
            return
        }
        val cache: Cache? = resourceInfo.resourceMethod.getAnnotation(Cache::class.java)
            ?: resourceInfo.resourceClass.getAnnotation(Cache::class.java)

        if (cache != null)
            responseContext.headers.add(
                "Cache-Control",
                cache.toCacheControl().combineWith(requestContext.cacheControl).toString()
            )
    }

    private fun isCacheable(responseContext: ContainerResponseContext): Boolean {
        val status = responseContext.status
        return status in 200..299 || status == 304
    }
}

private val ContainerRequestContext.cacheControl: CacheControl
    get() {
        val header = headers["Cache-Control"]
        return try {
            if (header != null) {
                CacheControl.valueOf(header.first())
            } else {
                CacheControl().apply { isNoTransform = false }
            }
        } catch (illegal: IllegalArgumentException) {
            CacheControl().apply { isNoTransform = false }
        }
    }
