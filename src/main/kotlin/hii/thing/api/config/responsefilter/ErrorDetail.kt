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

import hii.thing.api.getLogger
import hii.thing.api.logLevel
import org.apache.logging.log4j.Level
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class ErrorDetail(
    val code: Int,
    val message: String?,
    t: Throwable
) {
    val tType = t::class.java.simpleName
    private var t: Throwable? = null

    init {
        if (logLevel != Level.INFO) {
            this.t = t
        }
        getLogger().debug("${t.message}", t)
    }

    companion object {
        fun build(ex: WebApplicationException): Response {
            val err = ErrorDetail(ex.response.status, ex.message, ex)
            return Response.status(err.code).entity(err).type(MediaType.APPLICATION_JSON_TYPE).build()
        }
    }
}
