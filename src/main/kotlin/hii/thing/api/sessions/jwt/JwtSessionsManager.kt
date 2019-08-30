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

import hii.thing.api.getDao
import hii.thing.api.hashText
import hii.thing.api.security.JwtConst
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.SessionsManager
import hii.thing.api.sessions.dao.SessionsDao
import hii.thing.api.sessions.dao.recordsession.RecordSessionDao
import java.util.UUID

class JwtSessionsManager(
    val sessionsDao: SessionsDao = getDao(),
    val recordSessionManager: RecordSessionDao = getDao()
) : SessionsManager {

    override fun anonymousCreate(accessToken: String, deviceId: String): String {
        val jwt = JwtConst.decode(accessToken)
        require(jwt.subject == deviceId) { "ข้อมูล Device ไม่ตรงกับ Access token" }
        val session = UUID.randomUUID().toString()
        sessionsDao.save(accessToken.hashText(), session)
        return session
    }

    override fun create(accessToken: String, sessionDetail: CreateSessionDetail): String {
        val session = anonymousCreate(accessToken, sessionDetail.deviceId)
        recordSessionManager.register(session, sessionDetail)
        return session
    }

    override fun getBy(accessToken: String): String {
        return sessionsDao.get(accessToken.hashText())
    }

    override fun updateCreate(accessToken: String, sessionDetail: CreateSessionDetail): CreateSessionDetail {
        val jwt = JwtConst.decode(accessToken)
        require(jwt.subject == sessionDetail.deviceId)
        val session = getBy(accessToken)
        return recordSessionManager.update(session, sessionDetail)
    }

    override fun getDetail(session: String): CreateSessionDetail {
        return recordSessionManager.get(session)
    }
}
