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

package hii.thing.api.auth.dao.history

import redis.clients.jedis.HostAndPort
import redis.clients.jedis.Jedis

class RedisDeviceIdMapToAccessId(
    jedisClusterNodes: Set<HostAndPort>,
    val expireSec: Int = 60 * 30
) : DeviceIdMapToAccessId {

    // private val jc = JedisCluster(jedisClusterNodes) // for production.
    private val jc = Jedis(jedisClusterNodes.first()) // for unit test.

    override fun record(deviceId: String, accessId: String) {
        jc[accessId] = deviceId
        jc.expire(accessId, expireSec)
    }

    override fun getDeviceIdBy(issuedTo: String): String {
        val issued = jc[issuedTo]
        require(!issued.isNullOrEmpty())
        return issued
    }
}
