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
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class JwtConst private constructor() {
    companion object {
        val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(4096) }.genKeyPair()!!
        const val issuer = "auth.hii.in.th"

        fun decodeAndVerify(accessToken: String): DecodedJWT {
            var decodedJWT: DecodedJWT? = null
            runBlocking {
                launch { verify(accessToken) }
                launch { decodedJWT = decode(accessToken) }
            }
            return decodedJWT!!
        }

        fun decode(accessToken: String): DecodedJWT = JWT.decode(accessToken)

        fun verify(accessToken: String): Boolean {
            val publicKey: RSAPublicKey = JwtConst.keyPair.public as RSAPublicKey
            val privateKey: RSAPrivateKey = JwtConst.keyPair.private as RSAPrivateKey
            val algorithm = Algorithm.RSA512(publicKey, privateKey)

            val verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build() // Reusable verifier instance
            verifier.verify(accessToken)
            return true
        }
    }
}
