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

package hii.thing.api

import hii.thing.api.config.server
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option

class ThingApiServer(val args: Array<String>) {

    @Option(name = "-port", usage = "port destination to start server")
    private var port = 8080

    init {
        try {
            CmdLineParser(this).parseArgument(*args)
        } catch (cmd: CmdLineException) {
        }
    }

    fun start() {
        val server = server(port)
        server.start()
        server.join()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ThingApiServer(args).start()
        }
    }
}

