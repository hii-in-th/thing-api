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

package hii.thing.api.auth

import com.auth0.jwt.JWT
import com.google.gson.Gson
import hii.thing.api.InMemoryTestRule
import hii.thing.api.config.GsonJerseyProvider
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should not contain`
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application

class AccessTokenResourceTest : JerseyTest() {
    lateinit var mouckAccessTokenManager: AccessTokenManager
    val rule = InMemoryTestRule()

    /* ktlint-disable max-line-length */
    val deviceKey =
        """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJoaWkuaW4udGgiLCJzdWIiOiJOSFNPL2hpaWRlOSIsInJvbGUiOlsia2lvc2siLCJOU1REQSJdLCJuYmYiOjE1NjkyOTY5MTMsInNjb3BlIjpbIi9hdXRoIiwiL2RldmljZS9mYzRjNjgwNC1lNmM3LTQ5MjUtYjA5Yi0zMjI1YTkwMmYxNjgiXSwiaXNzIjoiYXV0aC5oaWkuaW4udGgiLCJpYXQiOjE1NjkyOTY5MTMsImp0aSI6ImZjNGM2ODA0LWU2YzctNDkyNS1iMDliLTMyMjVhOTAyZjE2OCJ9.8F9e68EOMP8GHVeA0lGNtcfDIFxk-tSJnVOPdSZv0RwUCyIID7_sL3AQRhDi8OdnPBbBKruZR0SClyeZKlFitw""".trimIndent()
    val accessToken =
        """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJoaWkuaW4udGgiLCJzdWIiOiJOSFNPL2hpaWRlOSIsInJvbGUiOlsiZmM0YzY4MDQtZTZjNy00OTI1LWIwOWItMzIyNWE5MDJmMTY4Iiwia2lvc2siLCJOU1REQSJdLCJuYmYiOjE1NjkyOTcwMjIsInNjb3BlIjpbIi9zZXNzaW9ucyIsIi9ibG9vZC1wcmVzc3VyZXMiLCIvaGVpZ2h0cyIsIi93ZWlnaHRzIiwiL3Jlc3VsdCIsIi9wZXJzb25zIl0sImlzcyI6ImF1dGguaGlpLmluLnRoIiwiZXhwIjoxNTY5Mjk4ODIyLCJpYXQiOjE1NjkyOTcwMjIsImp0aSI6ImJiMTFiNWUzLTVhOGUtNDc4MC04OGU3LWY1ZWJhNGM2MDhkYyJ9.u4vwqBCree4cu0izMofSzCfhVqEchF5B_sP1uWeZ926xRgHiVRhlUnRXy5ShBvWWSmUgpDXGl65V2IA5HnZSiw""".trimMargin()
    /* ktlint-enable max-line-length */

    override fun configure(): Application {
        mouckAccessTokenManager = object : AccessTokenManager {
            override fun create(deviceKey: String): AccessToken =
                if (deviceKey == this@AccessTokenResourceTest.deviceKey) AccessToken(accessToken)
                else throw NotFoundToken("Cannot found base token.")

            override fun get(accessToken: String): DeviceKeyDetail = DeviceKeyDetail("", "", listOf(), listOf())
            override fun getAccessId(accessToken: String): String = JWT.decode(accessToken).id
        }
        return ResourceConfig()
            .register(GsonJerseyProvider::class.java)
            .register(AccessTokenResource(mouckAccessTokenManager))
    }

    @Test
    fun createAccessTokenOK() {
        val res = target("/auth/tokens").request()
            .header("Authorization", "Bearer $deviceKey")
            .post(Entity.json(null))

        val accessTokenReq = Gson().fromJson(res.readEntity(String::class.java), AccessToken::class.java)

        accessTokenReq.accessToken `should equal` accessToken
    }

    @Test
    fun createAccessTokenNotAuthorization() {
        val res = target("/auth/tokens").request()
            .post(Entity.json(null))

        val bodyReturn = res.readEntity(String::class.java)

        res.status `should equal` 500
        bodyReturn `should not contain` accessToken
    }

    @Test
    fun createAccessTokenNotStartWithBearer() {
        val res = target("/auth/tokens").request()
            .header("Authorization", deviceKey)
            .post(Entity.json(null))

        val bodyReturn = res.readEntity(String::class.java)

        res.status `should equal` 500
        bodyReturn `should not contain` accessToken
    }

    @Test
    fun createAccessTokenNotKey() {
        val res = target("/auth/tokens").request()
            .header("Authorization", "Bearer ")
            .post(Entity.json(null))

        val bodyReturn = res.readEntity(String::class.java)

        res.status `should equal` 500
        bodyReturn `should not contain` accessToken
    }

    @Test
    fun createAccessTokenWrongKey() {
        val res = target("/auth/tokens").request()
            .header("Authorization", "Bearer keywronggggggg")
            .post(Entity.json(null))

        val bodyReturn = res.readEntity(String::class.java)

        res.status `should equal` 500
        bodyReturn `should not contain` accessToken
    }
}
