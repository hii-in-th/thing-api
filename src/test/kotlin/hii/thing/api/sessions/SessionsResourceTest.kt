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

package hii.thing.api.sessions

import com.google.gson.Gson
import hii.thing.api.JwtTestRule
import hii.thing.api.config.GsonJerseyProvider
import hii.thing.api.dao.lastresult.InMemoryLastResultDao
import hii.thing.api.sessions.mock.MockRoleTokenSecurity
import hii.thing.api.sessions.mock.MockTokenManager
import hii.thing.api.sessions.mock.MockTokenSecurityContext
import org.amshove.kluent.`should equal`
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test
import java.util.UUID
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType

class SessionsResourceTest : JerseyTest() {
    val rule = JwtTestRule()

    private val session = UUID.randomUUID().toString()
    private val deviceId = "aabbcc-aabbee"
    val devicename = "devices/105487111"

    /* ktlint-disable max-line-length */
    companion object {
        val mockTokenManager = MockTokenManager()
        val accessToken =
            """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJhdXRoLmhpaS5pbi50aCIsImlhdCI6MTU1OTcxMzA2NSwiZXhwIjoxNTU5NzE0MjY4LCJhdWQiOiJ2aXRhbC5oaWkuaW4udGgiLCJzdWIiOiJkZXZpY2VzLzEwNTQ4NzExMSIsInJvbGUiOiJraW9zayIsImp0aSI6ImE4Y2E1ZGUyLTg3NTItMTFlOS1iYzQyLTUyNmFmNzc2NGY2NCJ9.D9L65_f4dpFMkOpzuguWpg0fZq2olSOLaYqTVNXzslPFgVaLst6oqkZYRmaWrsWxXxTG0orCqIboovn3jeHhmg""".trimMargin()
    }
    /* ktlint-enable max-line-length */

    override fun configure(): Application {
        val mockSessionsManager = object : SessionsManager {
            override fun anonymousCreate(accessToken: String, deviceId: String): String = session

            override fun create(accessToken: String, sessionDetail: CreateSessionDetail): String = session

            override fun getBy(accessToken: String): String = session

            override fun updateCreate(accessToken: String, sessionDetail: CreateSessionDetail): CreateSessionDetail =
                CreateSessionDetail(deviceId, "", "", "")

            override fun getDetail(session: String): CreateSessionDetail =
                CreateSessionDetail(deviceId, "", "", "")
        }

        val sessionsResource = SessionsResource(mockSessionsManager, InMemoryLastResultDao())
        sessionsResource.context = MockTokenSecurityContext(accessToken, mockTokenManager)

        return ResourceConfig()
            .register(GsonJerseyProvider::class.java)
            .register(MockRoleTokenSecurity::class.java)
            .register(RolesAllowedDynamicFeature::class.java)
            .register(sessionsResource)
    }

    @Test
    fun sessionsOk() {
        val sessionDetail = CreateSessionDetail(deviceId, "1234", "CARD", "1111-09-65", "ธนชัย ชัย")

        val res = target("/sessions").request()
            .header("Authorization", "Bearer $accessToken")
            .header("X-Requested-By", devicename)
            .post(Entity.entity(Gson().toJson(sessionDetail), MediaType.APPLICATION_JSON_TYPE))

        val sessionResponse = Gson().fromJson<Session>(res.readEntity(String::class.java), Session::class.java)

        res.status `should equal` 200
        sessionResponse.sessionId `should equal` session
    }

    @Test
    fun sessionsNotUserInRole() {
        val sessionDetail = CreateSessionDetail(deviceId, "1234", "CARD", "1111-09-65")
        val res = target("/sessions").request()
            .header("Authorization", "Bearer sdfsdf")
            .header("X-Requested-By", devicename)
            .post(Entity.entity(Gson().toJson(sessionDetail), MediaType.APPLICATION_JSON_TYPE))

        res.status `should equal` 403
    }

    @Test
    fun tokenByQueryParameter() {
        val sessionDetail = CreateSessionDetail(deviceId, "1234", "CARD", "1111-09-65")
        val res = target("/sessions")
            .queryParam("token", accessToken)
            .request()
            .header("X-Requested-By", devicename)
            .post(Entity.entity(Gson().toJson(sessionDetail), MediaType.APPLICATION_JSON_TYPE))

        res.status `should equal` 200
    }
}
