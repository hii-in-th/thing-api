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

import com.google.gson.Gson
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

    /* ktlint-disable max-line-length */
    val baseKey =
        """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJhdXRoLmhpaS5pbi50aCIsImlhdCI6MTU1OTcxMzA2NSwiZXhwIjoxNTkxMjQ5MTk3LCJhdWQiOiJhdXRoLmhpaS5pbi50aCIsInN1YiI6ImRldmljZXMvMTA1NDg3MTExIiwicm9sZSI6Imtpb3NrIiwianRpIjoiNWI5YmNmNDMtZWRhOC00MjAwLWI5MzgtY2RiMDUwNjMxMmRkIn0.YpGLky41UHwtLGBlbhUUYerKz0SD0-Ff3PapUde-JhDphf9LzfiJ9NfCjUdqagat7YY_HAWv_RJf6xNUcl3ZLw""".trimIndent()
    val accessToken =
        """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJhdXRoLmhpaS5pbi50aCIsImlhdCI6MTU1OTcxMzA2NSwiZXhwIjoxNTU5NzE0MjY4LCJhdWQiOiJ2aXRhbC5oaWkuaW4udGgiLCJzdWIiOiJkZXZpY2VzLzEwNTQ4NzExMSIsInJvbGUiOiJraW9zayIsImp0aSI6ImE4Y2E1ZGUyLTg3NTItMTFlOS1iYzQyLTUyNmFmNzc2NGY2NCJ9.D9L65_f4dpFMkOpzuguWpg0fZq2olSOLaYqTVNXzslPFgVaLst6oqkZYRmaWrsWxXxTG0orCqIboovn3jeHhmg""".trimMargin()
    /* ktlint-enable max-line-length */

    override fun configure(): Application {
        mouckAccessTokenManager = object : AccessTokenManager {
            override fun create(baseToken: String): AccessToken {
                if (baseToken == baseKey)
                    return AccessToken(accessToken)
                else
                    throw NotFoundToken("Cannot found base token.")
            }
        }
        return ResourceConfig()
            .register(GsonJerseyProvider::class.java)
            .register(AccessTokenResource(mouckAccessTokenManager))
    }

    @Test
    fun createAccessTokenOK() {
        val res = target("/auth/tokens").request()
            .header("Authorization", "Bearer $baseKey")
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
            .header("Authorization", baseKey)
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
