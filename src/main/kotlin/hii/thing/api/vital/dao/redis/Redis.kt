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

package hii.thing.api.vital.dao.redis

import redis.clients.jedis.HostAndPort
import redis.clients.jedis.Jedis

class Redis(jedisClusterNodes: Set<HostAndPort>, val expireSec: Int) {
    // private val jc = JedisCluster(jedisClusterNodes) // for production.
    private val jc = Jedis(jedisClusterNodes.first()) // for unit test.

    fun set(key: String, value: String) {
        jc[key] = value
        jc.expire(key, expireSec)
    }

    fun get(key: String): String {
        return jc[key]
    }

    fun remove(key: String) {
        jc.del(key)
    }

    fun removeAll() {
        jc.flushDB()
    }
}
