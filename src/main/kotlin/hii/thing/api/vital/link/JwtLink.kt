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

package hii.thing.api.vital.link

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hii.thing.api.security.JwtConst
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.UUID

class JwtLink : Link {
    override fun create(refId: String): String {

        val jwtId = UUID.randomUUID().toString()

        val publicKey: RSAPublicKey = JwtConst.keyPair.publicKey
        val privateKey: RSAPrivateKey = JwtConst.keyPair.privateKey
        val algorithm = Algorithm.RSA512(publicKey, privateKey)
        val date = Date()
        val tokenAgeDay = 30

        return JWT.create()
            .withIssuer(JwtConst.issuer)
            .withIssuedAt(date)
            .withExpiresAt(Date((tokenAgeDay * 86400000L) + date.time))
            .withAudience(JwtConst.audience)
            .withJWTId(jwtId)
            .withArrayClaim("role", arrayOf("report"))
            .withArrayClaim("scope", arrayOf("/result"))
            .withClaim("ref", refId)
            .withSubject("Anonymous")
            .withNotBefore(date)
            .sign(algorithm)
    }

    override fun getRefId(link: String): String? {
        return JwtConst.decode(link).getClaim("ref").asString()
    }
}
