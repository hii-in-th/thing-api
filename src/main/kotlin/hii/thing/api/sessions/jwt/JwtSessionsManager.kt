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

import hii.thing.api.dao.getDao
import hii.thing.api.dao.registerstore.RegisterStoreDao
import hii.thing.api.dao.session.SessionsDao
import hii.thing.api.hashText
import hii.thing.api.security.token.JwtPrincipal
import hii.thing.api.sessions.CreateSessionDetail
import hii.thing.api.sessions.SessionsManager
import java.security.Principal
import java.util.UUID

class JwtSessionsManager(
    val sessionsDao: SessionsDao = getDao(),
    val registerStoreManager: RegisterStoreDao = getDao()
) : SessionsManager {

    override fun anonymousCreate(principal: Principal, deviceId: String): String {
        val jwt = (principal as JwtPrincipal).jwt
        require(jwt.subject == deviceId) { "ข้อมูล Device ไม่ตรงกับ Access token" }
        val session = UUID.randomUUID().toString()
        sessionsDao.save(principal.name.hashText(), session)
        return session
    }

    override fun create(principal: Principal, sessionDetail: CreateSessionDetail): String {
        val session = anonymousCreate(principal, sessionDetail.deviceId)
        registerStoreManager.register(session, sessionDetail)
        return session
    }

    override fun getBy(principal: Principal): String {
        return sessionsDao.get(principal.name.hashText())
    }

    override fun updateCreate(principal: Principal, sessionDetail: CreateSessionDetail): CreateSessionDetail {
        val jwt = (principal as JwtPrincipal).jwt
        require(jwt.subject == sessionDetail.deviceId)
        val session = getBy(principal)
        return registerStoreManager.update(session, sessionDetail)
    }

    override fun getDetail(session: String): CreateSessionDetail {
        return registerStoreManager.get(session)
    }
}
