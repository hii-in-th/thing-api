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

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.TokenExpiredException
import hii.thing.api.dao.keyspair.InMemoryRSAKeyPairDao
import hii.thing.api.security.JwtConst
import hii.thing.api.security.keypair.KeyPairManage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.UUID

class JwtConstTest {

    @Before
    fun setUp() {
        KeyPairManage.setUp(InMemoryRSAKeyPairDao())
    }

    @Test
    fun verifyOk() {
        val accessToken = createAccessToken()
        JwtConst.verify(accessToken)
    }

    @Test
    fun verfyScopeOk() {
        val accessToken = createAccessToken()
        JwtConst.verifyPath(accessToken, "vital")
    }

    @Test
    fun verfySubScopeOk() {
        val accessToken = createAccessToken()
        JwtConst.verifyPath(accessToken, "vital/result")
    }

    @Test(expected = Exception::class)
    fun verfySubScopeNotOk() {
        val accessToken = createAccessToken()
        JwtConst.verifyPath(accessToken, "run/result")
    }

    @Test(expected = Exception::class)
    fun verfyScopeNotOk() {
        val accessToken = createAccessToken()
        JwtConst.verifyPath(accessToken, "session")
    }

    @Test(expected = TokenExpiredException::class)
    fun verifyExpire() {
        runBlocking {
            val accessToken = createAccessToken(expire = 0)
            delay(1050)
            JwtConst.verify(accessToken)
        }
    }

    @Test(expected = InvalidClaimException::class)
    fun verifyWongIssuer() {
        val accessToken = createAccessToken(issuer = "mvp")
        Thread.sleep(10)
        JwtConst.verify(accessToken)
    }

    @Test
    fun decodeAndVerify() {
        val accessToken = createAccessToken()
        val decode = JwtConst.decodeAndVerify(accessToken)

        decode.issuer `should be equal to` JwtConst.issuer
        decode.claims["string"]!!.asString() `should be equal to` "thanachai"
        decode.claims["int"]!!.asInt() `should be equal to` 10
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
            .withClaim("int", 10)
            .withClaim("string", "thanachai")
            .withArrayClaim("scope", arrayOf("/vital", "/bmi", "/height"))
            .sign(algorithm)
    }
}
