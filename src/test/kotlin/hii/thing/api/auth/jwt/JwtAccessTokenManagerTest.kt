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

package hii.thing.api.auth.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hii.thing.api.JwtConst
import hii.thing.api.auth.Device
import hii.thing.api.auth.NotFoundToken
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class JwtAccessTokenManagerTest {

    /* ktlint-disable max-line-length */
    val baseKey =
        """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJhdXRoLmhpaS5pbi50aCIsImlhdCI6MTU1OTcxMzA2NSwiZXhwIjoxNTkxMjQ5MTk3LCJhdWQiOiJhdXRoLmhpaS5pbi50aCIsInN1YiI6ImRldmljZXMvMTA1NDg3MTExIiwicm9sZSI6Imtpb3NrIiwianRpIjoiNWI5YmNmNDMtZWRhOC00MjAwLWI5MzgtY2RiMDUwNjMxMmRkIn0.YpGLky41UHwtLGBlbhUUYerKz0SD0-Ff3PapUde-JhDphf9LzfiJ9NfCjUdqagat7YY_HAWv_RJf6xNUcl3ZLw""".trimIndent()
    /* ktlint-enable max-line-length */

    val tokenDao = object : TokenDao {
        override fun getDeviceBy(baseToken: String): Device {
            return if (baseToken == baseKey) Device(
                "hii/d121",
                baseToken,
                "api.ffc.in.th",
                listOf("kios"),
                listOf("sdf")
            )
            else
                throw NotFoundToken("Not found token")
        }
    }
    val jwtAccessTokenManager = JwtAccessTokenManager(tokenDao)

    @Test
    fun createAndJWTVerifier() {
        val accessToken = jwtAccessTokenManager.create(baseKey).accessToken

        verify(accessToken)
    }

    @Test
    fun createAndDecode() {
        val accessToken = jwtAccessTokenManager.create(baseKey).accessToken
        val jwtDecode = JWT.decode(accessToken)

        jwtDecode.subject!! `should be equal to` "hii/d121"
        jwtDecode.audience.first() `should be equal to` "api.ffc.in.th"
    }

    private fun verify(accessToken: String) {
        val publicKey: RSAPublicKey = JwtConst.keyPair.public as RSAPublicKey
        val privateKey: RSAPrivateKey = JwtConst.keyPair.private as RSAPrivateKey
        val algorithm = Algorithm.RSA512(publicKey, privateKey)

        val verifier = JWT.require(algorithm)
            .withIssuer(JwtConst.issuer)
            .build() // Reusable verifier instance
        verifier.verify(accessToken)
    }
}
