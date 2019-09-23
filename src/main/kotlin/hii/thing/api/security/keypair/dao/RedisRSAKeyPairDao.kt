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

package hii.thing.api.security.keypair.dao

import redis.clients.jedis.HostAndPort
import redis.clients.jedis.Jedis
import java.math.BigInteger
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class RedisRSAKeyPairDao(jedisClusterNodes: Set<HostAndPort>) : RSAKeyPairDao {
    // private val jc = JedisCluster(jedisClusterNodes) // for production.
    private val perfix = "hiiapi"
    private val jc = Jedis(jedisClusterNodes.first()) // for unit test.

    override var privateKey: RSAPrivateKey?
        get() =
            if (jc["$perfix-alPr"] == null) null
            else object : RSAPrivateKey {
                override fun getAlgorithm(): String = jc["$perfix-alPr"]
                override fun getEncoded(): ByteArray = jc["$perfix-enPr".toByteArray()]
                override fun getPrivateExponent(): BigInteger = BigInteger(jc["$perfix-exPr".toByteArray()])
                override fun getModulus(): BigInteger = BigInteger(jc["$perfix-moPr".toByteArray()])
                override fun getFormat(): String = jc["$perfix-foPr"]
            }
        set(value) {
            jc["$perfix-alPr"] = value!!.algorithm
            jc["$perfix-enPr".toByteArray()] = value.encoded
            jc["$perfix-exPr".toByteArray()] = value.privateExponent.toByteArray()
            jc["$perfix-moPr".toByteArray()] = value.modulus.toByteArray()
            jc["$perfix-foPr"] = value.format
        }
    override var publicKey: RSAPublicKey?
        get() =
            if (jc["$perfix-alPu"] == null) null
            else
                object : RSAPublicKey {
                    override fun getAlgorithm(): String = jc["$perfix-alPu"]
                    override fun getEncoded(): ByteArray = jc["$perfix-enPu".toByteArray()]
                    override fun getModulus(): BigInteger = BigInteger(jc["$perfix-moPu".toByteArray()])
                    override fun getPublicExponent(): BigInteger = BigInteger(jc["$perfix-exPu".toByteArray()])
                    override fun getFormat(): String = jc["$perfix-foPu"]
                }
        set(value) {
            jc["$perfix-alPu"] = value!!.algorithm
            jc["$perfix-enPu".toByteArray()] = value.encoded
            jc["$perfix-moPu".toByteArray()] = value.modulus.toByteArray()
            jc["$perfix-exPu".toByteArray()] = value.publicExponent.toByteArray()
            jc["$perfix-foPu"] = value.format
        }
}
