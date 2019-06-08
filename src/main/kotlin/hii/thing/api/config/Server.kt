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

import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.LowResourceMonitor
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.thread.QueuedThreadPool

fun server(port: Int): Server {

    return Server(threadPool).apply {
        handler = ServletBuilder.build()
        connectors = connectorFor(this, "0.0.0.0", port)
        addBean(lowResourceMonitorFor(this))
    }
}

private val MAX_THREADS = 500
private val MIN_THREADS = 50
private val IDLE_TIMEOUT = 6000

private val threadPool: QueuedThreadPool
    get() {
        val threadPool = QueuedThreadPool(MAX_THREADS, MIN_THREADS, IDLE_TIMEOUT)
        threadPool.isDaemon = true
        threadPool.isDetailedDump = false
        return threadPool
    }

private fun lowResourceMonitorFor(server: Server): LowResourceMonitor {
    val monitor = LowResourceMonitor(server)
    monitor.period = 1000
    monitor.lowResourcesIdleTimeout = 1000
    monitor.monitorThreads = true
    monitor.maxMemory = 0
    monitor.maxLowResourcesTime = 5000
    return monitor
}

private fun connectorFor(server: Server, host: String, port: Int): Array<Connector> {
    val connector = ServerConnector(server)
    connector.host = host
    connector.port = port
    connector.idleTimeout = 30000
    connector.acceptQueueSize = 3000
    return arrayOf(connector)
}
