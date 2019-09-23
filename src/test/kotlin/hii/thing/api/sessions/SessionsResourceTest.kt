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
import hii.thing.api.InMemoryTestRule
import hii.thing.api.config.GsonJerseyProvider
import hii.thing.api.sessions.CreateSessionDetail.InputType.CARD
import hii.thing.api.sessions.CreateSessionDetail.InputType.UNDEFINED
import hii.thing.api.sessions.dao.recordsession.InMemoryRecordSessionDao
import hii.thing.api.sessions.mock.MockRoleTokenSecurity
import hii.thing.api.sessions.mock.MockTokenManager
import hii.thing.api.sessions.mock.MockTokenSecurityContext
import hii.thing.api.vital.dao.lastresult.InMemoryLastResultDao
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
    val rule = InMemoryTestRule()

    private val session = UUID.randomUUID().toString()
    private val deviceId = "1e8aaf6c-7748-4bac-9d98-8f5d1a840689"
    val devicename = "dev/007"

    /* ktlint-disable max-line-length */
    companion object {
        val mockTokenManager = MockTokenManager()
        val accessToken =
            """eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJhdWQiOiJoaWkuaW4udGgiLCJzdWIiOiJkZXYvMDA3Iiwicm9sZSI6WyIxZThhYWY2Yy03NzQ4LTRiYWMtOWQ5OC04ZjVkMWE4NDA2ODkiLCJraW9zayJdLCJuYmYiOjE1NjgxNzM1MzksInNjb3BlIjpbIi9zZXNzaW9ucyIsIi9ibG9vZC1wcmVzc3VyZXMiLCIvaGVpZ2h0cyIsIi93ZWlnaHRzIiwiL3Jlc3VsdCIsIi9wZXJzb25zIiwiL2RldmljZSJdLCJpc3MiOiJhdXRoLmhpaS5pbi50aCIsImV4cCI6MTU2ODE3NTMzOSwiaWF0IjoxNTY4MTczNTM5LCJqdGkiOiJhYTFiMzM1Ny1jOTZlLTQ0ZGMtYjVjYS0xODgxODEwNjI0YzQifQ.HPWp18RnqeTZt7qbozvzXjwJunNUMMXWhOCXKbWY6QFE59h_HRpENy-CluQBcYLtq5gg7EiQ1spRgGl2zkv5chD7Xk8N_I78XzSBMtGuLck3QKgNZNP5P4R2XzGll5X_ynZxbTFaMhHhKsKHtDAYLkWG7Gx1AI9AfNb1585Lnj7H3bw7-av5uRLlfZAJmkYWz1EIux-C9RGC6IYY7BYjqxhmpTF9LWR-qrDGJ0-5Q4bZgk3Cy75O11N0XiSDb6TZVmZ5eokqh_D9lPriJyk-PUYNW3hb2N4lhCyHU40KlWH1wYMxt1N66igMDRiSglWkEV1b7xA0kNtqc-3023tL1EqAEuZw7vgJXKRMJfFmCAGA60hBK2bg3tRnIJGLUEhBIIkrCz6YZxoOzbCy6lEaqw9OqvMH-1twFd09nhoL6IAcLj0EdVAgTU-rDrSL2G-xfWJS5Qo2XWfIsp1TlDdnhcU3uR5Id5ds27Izgszhu_5OKZeln-iYZDLOyogAO606mTDcxjBf2b5XOwVB_i_eNyrdH47DwlTruMdraJ5tJKMmko1P4K8aPFfzMdrYSunZQD3tMk3203_abEBQ9zm5pyWwO4gWZnREBvXYPAZCYIyyR7ScEiBiC04k61kvPvbien1AWnf1rCvSwRipIw7qqkUwS8ggzVHJ4dT5TpN11V0""".trimMargin()
    }
    /* ktlint-enable max-line-length */

    override fun configure(): Application {
        val mockSessionsManager = object : SessionsManager {
            override fun anonymousCreate(accessToken: String, deviceId: String): String = session

            override fun create(accessToken: String, sessionDetail: CreateSessionDetail): String = session

            override fun getBy(accessToken: String): String = session

            override fun updateCreate(accessToken: String, sessionDetail: CreateSessionDetail): CreateSessionDetail =
                CreateSessionDetail(deviceId, "", UNDEFINED, "")

            override fun getDetail(session: String): CreateSessionDetail =
                CreateSessionDetail(deviceId, "", UNDEFINED, "")
        }

        val sessionsResource =
            SessionsResource(mockSessionsManager, InMemoryLastResultDao(), InMemoryRecordSessionDao())
        sessionsResource.context = MockTokenSecurityContext(accessToken, mockTokenManager)

        return ResourceConfig()
            .register(GsonJerseyProvider::class.java)
            .register(MockRoleTokenSecurity::class.java)
            .register(RolesAllowedDynamicFeature::class.java)
            .register(sessionsResource)
    }

    @Test
    fun sessionsOk() {
        val sessionDetail = CreateSessionDetail(deviceId, "1234", CARD, "1111-09-65", "ธนชัย ชัย")

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
        val sessionDetail = CreateSessionDetail(deviceId, "1234", CARD, "1111-09-65")
        val res = target("/sessions").request()
            .header("Authorization", "Bearer sdfsdf")
            .header("X-Requested-By", devicename)
            .post(Entity.entity(Gson().toJson(sessionDetail), MediaType.APPLICATION_JSON_TYPE))

        res.status `should equal` 403
    }

    @Test
    fun tokenByQueryParameter() {
        val sessionDetail = CreateSessionDetail(deviceId, "1234", CARD, "1111-09-65")
        val res = target("/sessions")
            .queryParam("token", accessToken)
            .request()
            .header("X-Requested-By", devicename)
            .post(Entity.entity(Gson().toJson(sessionDetail), MediaType.APPLICATION_JSON_TYPE))

        res.status `should equal` 200
    }
}
