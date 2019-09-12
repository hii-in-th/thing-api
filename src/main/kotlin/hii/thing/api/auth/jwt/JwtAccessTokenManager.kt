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
import hii.thing.api.auth.AccessToken
import hii.thing.api.auth.AccessTokenManager
import hii.thing.api.auth.DeviceKeyDetail
import hii.thing.api.auth.dao.devicekey.DeviceKeyDao
import hii.thing.api.getDao
import hii.thing.api.getLogger
import hii.thing.api.security.JwtConst
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.LinkedList
import java.util.UUID

class JwtAccessTokenManager(
    private val deviceKeyDao: DeviceKeyDao = getDao()
) : AccessTokenManager {
    override fun create(deviceKey: String): AccessToken {
        val device = deviceKeyDao.getDeviceBy(deviceKey)
        val jwtId = UUID.randomUUID().toString()

        val publicKey: RSAPublicKey = JwtConst.keyPair.publicKey
        val privateKey: RSAPrivateKey = JwtConst.keyPair.privateKey
        val algorithm = Algorithm.RSA512(publicKey, privateKey)
        val date = Date()

        val role = LinkedList<String>()
        role.addAll(device.roles)
        role.addFirst(device.deviceID)

        val accessToken = JWT.create()
            .withIssuer(JwtConst.issuer)
            .withIssuedAt(date)
            .withExpiresAt(Date(date.time + 1800000))
            .withAudience(JwtConst.audience)
            .withSubject(device.deviceName)
            .withJWTId(jwtId)
            .withArrayClaim("role", role.toTypedArray())
            .withArrayClaim(
                "scope",
                arrayOf(
                    "/sessions",
                    "/blood-pressures",
                    "/heights",
                    "/weights",
                    "/result",
                    "/persons"
                )
            )
            .withNotBefore(date)
            .sign(algorithm)

        logger.info { "Register access token by ${device.deviceName} jwtId:$jwtId" }

        return AccessToken(accessToken)
    }

    override fun get(accessToken: String): DeviceKeyDetail {
        val decode = JWT().decodeJwt(accessToken)
        val deviceName = decode.subject!!
        val roles = decode.claims["role"]?.asArray(String::class.java)?.toMutableList() ?: mutableListOf()
        val scope = listOf("Undefined") // ไม่สามารถดึง scope เดิมของ Base token ได้
        val deviceKeyId = roles.first()!!
        roles.removeAt(0)

        return DeviceKeyDetail(deviceName, "Hide", roles, scope, deviceKeyId)
    }

    override fun getAccessId(accessToken: String): String = JWT.decode(accessToken).id

    companion object {
        private val logger by lazy { getLogger() }
    }
}
