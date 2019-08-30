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

package hii.thing.api.auth.dao.devicetoken

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hii.thing.api.auth.DeviceToken
import hii.thing.api.security.JwtConst
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.UUID

class JwtDeviceTokenDao : DeviceTokenDao {
    override fun getDeviceBy(baseToken: String): DeviceToken {
        val jwt = JwtConst.decodeAndVerify(baseToken)

        val role = jwt.claims["role"]!!.asArray(String::class.java).toList()
        val scope = jwt.claims["scope"]!!.asArray(String::class.java).toList()
        return DeviceToken(jwt.subject, baseToken, role, scope)
    }

    /**
     * Device token not expire.
     */
    override fun registerDevice(deviceToken: DeviceToken): DeviceToken {
        require(deviceToken.baseToken.isBlank()) { "Jwt device token auto gen base token. baseToken is bank." }
        val jwtId = UUID.randomUUID().toString()

        val publicKey: RSAPublicKey = JwtConst.keyPair.publicKey
        val privateKey: RSAPrivateKey = JwtConst.keyPair.privateKey
        val algorithm = Algorithm.RSA512(publicKey, privateKey)
        val date = Date()

        val baseToken = JWT.create()
            .withIssuer(JwtConst.issuer)
            .withIssuedAt(date)
            .withAudience(JwtConst.audience)
            .withSubject(deviceToken.deviceName)
            .withJWTId(jwtId)
            .withArrayClaim("role", deviceToken.roles.toTypedArray())
            .withArrayClaim("scope", deviceToken.scope.toTypedArray())
            .withNotBefore(date)
            .sign(algorithm)

        return getDeviceBy(baseToken)
    }
}