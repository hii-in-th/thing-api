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
import hii.thing.api.auth.AccessToken
import hii.thing.api.auth.AccessTokenManager
import hii.thing.api.getLogger
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.UUID

// TODO ยังไม่ได้กำหนด tokenDao
class JwtAccessTokenManager(val tokenDao: TokenDao) : AccessTokenManager {
    override fun create(baseToken: String): AccessToken {
        val device = tokenDao.getDeviceBy(baseToken)
        val jwtId = UUID.randomUUID().toString()

        val publicKey: RSAPublicKey = JwtConst.keyPair.public as RSAPublicKey
        val privateKey: RSAPrivateKey = JwtConst.keyPair.private as RSAPrivateKey
        val algorithm = Algorithm.RSA512(publicKey, privateKey)
        val date = Date()

        val accessToken = JWT.create()
            .withIssuer(JwtConst.issuer)
            .withIssuedAt(date)
            .withExpiresAt(Date(date.time + 323234))
            .withAudience(device.audience)
            .withSubject(device.deviceName)
            .withJWTId(jwtId)
            .withArrayClaim("role", device.roles.toTypedArray())
            .withArrayClaim("scopt", device.scope.toTypedArray())
            .withNotBefore(date)
            .sign(algorithm)

        logger.info("Register access token by ${device.deviceName} jwtId:$jwtId")

        return AccessToken(accessToken)
    }

    companion object {
        private val logger by lazy { getLogger() }
    }
}
