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

package hii.thing.api.config

import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.servlet.ServletContainer

internal object ServletBuilder {

    val PATH_SPEC = "/v1/*"
    val ROOT_PATH = ""

    fun build(): ServletContextHandler {
        return ServletContextHandler(ServletContextHandler.SESSIONS).apply {
            contextPath = ROOT_PATH
            addServlet(thingServletHolder(), PATH_SPEC)
        }
    }

    private fun thingServletHolder(): ServletHolder {
        return ServletHolder(ServletContainer(ServletResourceConfig())).apply {
            initOrder = 0
        }
    }
}
