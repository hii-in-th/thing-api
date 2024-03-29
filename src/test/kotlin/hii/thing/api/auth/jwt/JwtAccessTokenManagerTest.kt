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
import hii.thing.api.InMemoryTestRule
import hii.thing.api.auth.DeviceKeyDetail
import hii.thing.api.auth.NotFoundToken
import hii.thing.api.auth.dao.devicekey.DeviceKeyDao
import hii.thing.api.security.JwtConst
import hii.thing.api.security.keypair.KeyPairManage
import hii.thing.api.security.keypair.dao.DemoRSAKeyPairDao
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain all`
import org.junit.Before
import org.junit.Test

class JwtAccessTokenManagerTest {
    val rule = InMemoryTestRule()

    /* ktlint-disable max-line-length */
    val baseKey =
        """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJhdXRoLmhpaS5pbi50aCIsImlhdCI6MTU1OTcxMzA2NSwiZXhwIjoxNTkxMjQ5MTk3LCJhdWQiOiJhdXRoLmhpaS5pbi50aCIsInN1YiI6ImRldmljZXMvMTA1NDg3MTExIiwicm9sZSI6WyJraW9zayJdLCJzY29wZSI6WyIvYXV0aCJdLCJqdGkiOiI1YjliY2Y0My1lZGE4LTQyMDAtYjkzOC1jZGIwNTA2MzEyZGQifQ.hOqkXnTsSjEVMJgLJHIxwnzZZdSISlPt9gUpp6bAVlDc_hfPnnX3puqs_MF_z7vy_-26acpCbqDboaMDPXUhFQ""".trimIndent()
    /* ktlint-enable max-line-length */

    private val deviceKeyDao = object : DeviceKeyDao {
        override fun getDeviceBy(deviceKey: String): DeviceKeyDetail {
            return if (deviceKey == baseKey) {
                val decode = JWT.decode(baseKey)!!

                val deviceName = decode.subject!!
                val roles = decode.claims["role"]?.asArray(String::class.java)?.toList() ?: listOf()
                val scope = decode.claims["scope"]?.asArray(String::class.java)?.toList() ?: listOf()
                DeviceKeyDetail(
                    deviceName,
                    deviceKey,
                    roles,
                    scope,
                    decode.id
                )
            } else
                throw NotFoundToken("Not found token")
        }

        override fun registerDevice(deviceKeyDetail: DeviceKeyDetail): DeviceKeyDetail = deviceKeyDetail
    }
    private val jwtAccessTokenManager = JwtAccessTokenManager(deviceKeyDao)

    @Before
    fun setUp() {
        KeyPairManage.setUp(DemoRSAKeyPairDao())
    }

    @Test
    fun createAndJWTVerifier() {
        val accessToken = jwtAccessTokenManager.create(baseKey).accessToken
        JwtConst.verify(accessToken)
    }

    @Test
    fun createAndDecode() {
        val accessToken = jwtAccessTokenManager.create(baseKey).accessToken
        val jwtDecode = JWT.decode(accessToken)

        jwtDecode.subject!! `should be equal to` "devices/105487111"
        jwtDecode.audience.first() `should be equal to` JwtConst.audience
    }

    @Test
    fun get() {
        val accessToken = jwtAccessTokenManager.create(baseKey).accessToken
        val keyDetail = jwtAccessTokenManager.get(accessToken)
        val decode = JWT.decode(baseKey)!!

        val deviceName = decode.subject!!
        val roles = decode.claims["role"]?.asArray(String::class.java)?.toList() ?: listOf()
        val deviceKeyId = decode.id

        keyDetail.deviceID `should be equal to` deviceKeyId
        keyDetail.deviceName `should be equal to` deviceName
        keyDetail.roles `should contain all` roles
    }
}
