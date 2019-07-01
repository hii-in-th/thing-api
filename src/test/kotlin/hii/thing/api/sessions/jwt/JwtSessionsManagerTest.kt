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

package hii.thing.api.sessions.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hii.thing.api.dao.keyspair.InMemoryRSAKeyPairDao
import hii.thing.api.dao.registerstore.InMemoryRegisterStoreDao
import hii.thing.api.dao.session.InMemorySessionDao
import hii.thing.api.security.JwtConst
import hii.thing.api.security.keypair.KeyPairManage
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.SessionsManager
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.UUID

class JwtSessionsManagerTest {
    val deviceId = "98439-32423-fgd-gfd-gdsg-fds"
    val sessionsManager: SessionsManager = JwtSessionsManager(InMemorySessionDao(), InMemoryRegisterStoreDao())
    val createDetail = CreateSessionDetail(deviceId, "1234", "CARD", "1111-09-65")
    val accessToken = createAccessToken()

    @Before
    fun setUp() {
        KeyPairManage.setUp(InMemoryRSAKeyPairDao())
    }

    @Test
    fun anonymousCreate() {
        val session = sessionsManager.anonymousCreate(accessToken, deviceId)
        session.length `should be greater than` 20
        println("Session $session")
    }

    @Test
    fun create() {
        val session = sessionsManager.create(accessToken, createDetail)
        session.length `should be greater than` 20
        println("Session $session")
    }

    @Test
    fun getBy() {
        val session = sessionsManager.create(accessToken, createDetail)

        sessionsManager.getBy(accessToken) `should be equal to` session
    }

    @Test(expected = Exception::class)
    fun getByEmptyFail() {
        sessionsManager.getBy(accessToken)
    }

    @Test
    fun updateCreate() {
        sessionsManager.create(accessToken, CreateSessionDetail(deviceId))
        val update = sessionsManager.updateCreate(accessToken, createDetail)

        update.citizenId `should equal` "1234"
    }

    @Test(expected = Exception::class)
    fun updateEmptyFail() {
        sessionsManager.updateCreate(accessToken, CreateSessionDetail(deviceId, null, null, null))
    }

    private fun createAccessToken(expire: Long = 1000000, issuer: String = JwtConst.issuer): String {
        val publicKey: RSAPublicKey = JwtConst.keyPair.publicKey
        val privateKey: RSAPrivateKey = JwtConst.keyPair.privateKey
        val algorithm = Algorithm.RSA512(publicKey, privateKey)
        val date = Date()

        return JWT.create()
            .withIssuer(issuer)
            .withIssuedAt(date)
            .withExpiresAt(Date(date.time + expire))
            .withJWTId(UUID.randomUUID().toString())
            .withNotBefore(date)
            .withSubject(deviceId)
            .withJWTId(UUID.randomUUID().toString())
            .withClaim("int", 10)
            .withClaim("string", "thanachai")
            .sign(algorithm)
    }
}
