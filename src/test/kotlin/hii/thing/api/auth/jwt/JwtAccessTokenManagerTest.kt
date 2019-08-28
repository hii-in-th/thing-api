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
import hii.thing.api.auth.Device
import hii.thing.api.auth.NotFoundToken
import hii.thing.api.dao.apikey.DeviceTokenDao
import hii.thing.api.dao.keyspair.DemoRSAKeyPairDao
import hii.thing.api.security.JwtConst
import hii.thing.api.security.keypair.KeyPairManage
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class JwtAccessTokenManagerTest {
    val rule = InMemoryTestRule()

    /* ktlint-disable max-line-length */
    val baseKey =
        """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJhdXRoLmhpaS5pbi50aCIsImlhdCI6MTU1OTcxMzA2NSwiZXhwIjoxNTkxMjQ5MTk3LCJhdWQiOiJhdXRoLmhpaS5pbi50aCIsInN1YiI6ImRldmljZXMvMTA1NDg3MTExIiwicm9sZSI6Imtpb3NrIiwianRpIjoiNWI5YmNmNDMtZWRhOC00MjAwLWI5MzgtY2RiMDUwNjMxMmRkIn0.YpGLky41UHwtLGBlbhUUYerKz0SD0-Ff3PapUde-JhDphf9LzfiJ9NfCjUdqagat7YY_HAWv_RJf6xNUcl3ZLw""".trimIndent()
    /* ktlint-enable max-line-length */

    val tokenDao = object : DeviceTokenDao {
        override fun getDeviceBy(baseToken: String): Device {
            return if (baseToken == baseKey) Device(
                "hii/d121",
                baseToken,
                listOf("kios"),
                listOf("sdf")
            )
            else
                throw NotFoundToken("Not found token")
        }

        override fun registerDevice(device: Device): Device = device
    }
    val jwtAccessTokenManager = JwtAccessTokenManager(tokenDao)

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

        jwtDecode.subject!! `should be equal to` "hii/d121"
        jwtDecode.audience.first() `should be equal to` JwtConst.audience
    }
}
