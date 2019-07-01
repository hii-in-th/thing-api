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

package hii.thing.api.security.keypair

import hii.thing.api.dao.keyspair.RSAKeyPairDao
import hii.thing.api.getLogger
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

object KeyPairManage : KeyPair {
    private lateinit var keyPairDao: RSAKeyPairDao

    fun setUp(dao: RSAKeyPairDao): KeyPair {
        if (kotlin.runCatching { keyPairDao }.isFailure)
            keyPairDao = dao
        return this
    }

    override val privateKey: RSAPrivateKey
        get() {
            val private = keyPairDao.privateKey
            return if (private != null) {
                private
            } else {
                genNewKeyPair()
                keyPairDao.privateKey!!
            }
        }

    override val publicKey: RSAPublicKey
        get() {
            val public = keyPairDao.publicKey
            return if (public != null) {
                public
            } else {
                genNewKeyPair()
                keyPairDao.publicKey!!
            }
        }

    private fun genNewKeyPair() {
        getLogger().info("Generate new key pari")
        val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(4096) }.genKeyPair()!!
        keyPairDao.privateKey = keyPair.private as RSAPrivateKey
        keyPairDao.publicKey = keyPair.public as RSAPublicKey
    }
}
