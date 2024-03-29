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

package hii.thing.api.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.RSAKeyProvider
import hii.thing.api.getDao
import hii.thing.api.security.keypair.KeyPair
import hii.thing.api.security.keypair.KeyPairManage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * Configuration และ util ของ JWT
 */
object JwtConst {
    val keyPair: KeyPair by lazy {
        if (KeyPairManage.isSetup())
            KeyPairManage
        else
            KeyPairManage.setUp(getDao())
    }
    const val issuer = "auth.hii.in.th"
    const val audience = "hii.in.th"

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
        val publicKey: RSAPublicKey = keyPair.publicKey
        val algorithm = Algorithm.RSA512(object : RSAKeyProvider {
            override fun getPrivateKeyId(): String = ""
            override fun getPrivateKey(): RSAPrivateKey? = null
            override fun getPublicKeyById(keyId: String?): RSAPublicKey = publicKey
        })

        val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build() // Reusable verifier instance
        verifier.verify(accessToken)
        return true
    }

    fun verifyPath(accessToken: String, path: String): Boolean {
        verify(accessToken)
        val scope = decode(accessToken).getClaim("scope").asArray(String::class.java)
        val contains = scope.find { "/$path".startsWith(it) } != null
        if (!contains) throw JWTVerificationException("Part $path not allow")
        return true
    }
}

fun DecodedJWT.role(): Array<String> = this.getClaim("role").asArray(String::class.java)
