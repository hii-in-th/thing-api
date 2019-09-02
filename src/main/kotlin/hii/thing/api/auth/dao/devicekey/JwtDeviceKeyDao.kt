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

package hii.thing.api.auth.dao.devicekey

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hii.thing.api.auth.DeviceKeyDetail
import hii.thing.api.security.JwtConst
import hii.thing.api.security.keypair.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.UUID

class JwtDeviceKeyDao(val keyPair: KeyPair = JwtConst.keyPair) : DeviceKeyDao {
    override fun getDeviceBy(deviceKey: String): DeviceKeyDetail {
        val jwt = JwtConst.decodeAndVerify(deviceKey)

        val role = jwt.claims["role"]!!.asArray(String::class.java).toList()
        val scope = jwt.claims["scope"]!!.asArray(String::class.java).toList()
        return DeviceKeyDetail(jwt.subject, deviceKey, role, scope, jwt.id)
    }

    /**
     * Device token not expire.
     */
    override fun registerDevice(deviceKeyDetail: DeviceKeyDetail): DeviceKeyDetail {
        require(deviceKeyDetail.deviceKey.isBlank()) { "Jwt device token auto gen base token. baseToken is bank." }
        val jwtId = UUID.randomUUID().toString()

        val publicKey: RSAPublicKey = keyPair.publicKey
        val privateKey: RSAPrivateKey = keyPair.privateKey
        val algorithm = Algorithm.RSA512(publicKey, privateKey)
        val date = Date()

        val baseToken = JWT.create()
            .withIssuer(JwtConst.issuer)
            .withIssuedAt(date)
            .withAudience(JwtConst.audience)
            .withSubject(deviceKeyDetail.deviceName)
            .withJWTId(jwtId)
            .withArrayClaim("role", deviceKeyDetail.roles.toTypedArray())
            .withArrayClaim("scope", deviceKeyDetail.scope.toTypedArray())
            .withNotBefore(date)
            .sign(algorithm)

        return getDeviceBy(baseToken)
    }
}
