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

import hii.thing.api.config.GsonJerseyProvider
import java.security.MessageDigest
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.core.EntityTag
import javax.ws.rs.ext.Provider

@Provider
class EtagFilter : ContainerResponseFilter {

    override fun filter(request: ContainerRequestContext, response: ContainerResponseContext) {
        if (response.status != 200) return
        if (!response.hasEntity()) return

        val hash = Etag(response.entity).value
        val etag = EntityTag(hash)

        val responseBuilder = request.request.evaluatePreconditions(etag)
        if (responseBuilder != null) {
            response.status = 304
            response.entity = null
        }
        response.headers.add("ETag", "\"$hash\"")
    }
}

class Etag(content: Any) {
    val value: String = GsonJerseyProvider.hiiGson.toJson(content).md5()
}

/**
 * From
 * https://github.com/kittinunf/Fuse/blob/master/fuse/src/main/kotlin/com/github/kittinunf/fuse/util/MD5.kt
 */
private fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digested = md.digest(toByteArray())
    return digested.joinToString("") {
        String.format("%02x", it)
    }
}
