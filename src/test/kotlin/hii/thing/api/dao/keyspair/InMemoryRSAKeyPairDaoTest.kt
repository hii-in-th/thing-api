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

package hii.thing.api.dao.keyspair

import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class InMemoryRSAKeyPairDaoTest {
    val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(512) }.genKeyPair()!!
    val memory = InMemoryRSAKeyPairDao()
    val dao: RSAKeyPairDao = memory

    @Test
    fun setAndGetPrivateKey() {
        val keypairPrivate = keyPair.private as RSAPrivateKey

        dao.privateKey = keypairPrivate
        dao.privateKey!!.algorithm `should equal` keypairPrivate.algorithm
        dao.privateKey!!.encoded `should equal` keypairPrivate.encoded
        dao.privateKey!!.privateExponent `should equal` keypairPrivate.privateExponent
        dao.privateKey!!.modulus `should equal` keypairPrivate.modulus
        dao.privateKey!!.format `should equal` keypairPrivate.format
    }

    @Test
    fun setAndGetPublicKey() {
        val keyPairPublic = keyPair.public as RSAPublicKey
        dao.publicKey = keyPairPublic
        dao.publicKey!!.algorithm `should equal` keyPairPublic.algorithm
        dao.publicKey!!.encoded `should equal` keyPairPublic.encoded
        dao.publicKey!!.modulus `should equal` keyPairPublic.modulus
        dao.publicKey!!.publicExponent `should equal` keyPairPublic.publicExponent
        dao.publicKey!!.format `should equal` keyPairPublic.format
    }

    @Before
    fun setUp() {
        memory.cleanAll()
    }
}
