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

package hii.thing.api.sessions.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hii.thing.api.JwtConst
import hii.thing.api.sessions.DeviceManager
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Date

class JwtDeviceManagerTest {

    val deviceManager: DeviceManager = JwtDeviceManager()
    val deviceId = "hii-007abc-dadlco-112or"

    @Test
    fun getDeviceIdFrom() {
        deviceManager.getDeviceIdFrom(createAccessToken()) `should be equal to` deviceId
    }

    private fun createAccessToken(expire: Long = 1000000, issuer: String = JwtConst.issuer): String {
        val publicKey: RSAPublicKey = JwtConst.keyPair.public as RSAPublicKey
        val privateKey: RSAPrivateKey = JwtConst.keyPair.private as RSAPrivateKey
        val algorithm = Algorithm.RSA512(publicKey, privateKey)
        val date = Date()

        return JWT.create()
            .withIssuer(issuer)
            .withIssuedAt(date)
            .withExpiresAt(Date(date.time + expire))
            .withJWTId(deviceId)
            .withNotBefore(date)
            .sign(algorithm)
    }
}
