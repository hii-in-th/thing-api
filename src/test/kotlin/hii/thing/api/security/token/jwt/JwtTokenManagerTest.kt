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

package hii.thing.api.security.token.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hii.thing.api.dao.keyspair.DemoRSAKeyPairDao
import hii.thing.api.security.JwtConst
import hii.thing.api.security.keypair.KeyPairManage
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.UUID

class JwtTokenManagerTest {

    val jwtTokenManager = JwtTokenManager()
    val roles = listOf("MACHINE", "abc")
    val scope = listOf("/regis", "/auth")
    val deviceName = "hii/007"

    @Before
    fun setUp() {
        KeyPairManage.setUp(DemoRSAKeyPairDao())
    }

    @Test
    fun verify() {
        val accessToken = createAccessToken()
        jwtTokenManager.verify(accessToken) `should be equal to` true
    }

    @Test
    fun getUserRole() {
        val accessToken = createAccessToken()
        jwtTokenManager.getUserRole(accessToken) `should equal` roles
    }

    @Test
    fun getName() {
        val accessToken = createAccessToken()
        jwtTokenManager.getName(accessToken) `should be equal to` deviceName
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
            .withSubject(deviceName)
            .withArrayClaim("role", roles.toTypedArray())
            .withArrayClaim("scope", scope.toTypedArray())
            .sign(algorithm)
    }
}
