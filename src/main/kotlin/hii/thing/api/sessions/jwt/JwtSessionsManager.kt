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

import hii.thing.api.dao.SessionsDao
import hii.thing.api.dao.session.RedisSessionDao
import hii.thing.api.hashText
import hii.thing.api.sessions.SessionsManager
import java.util.UUID

class JwtSessionsManager(
    val sessionsDao: SessionsDao = RedisSessionDao(setOf())
) : SessionsManager {

    override fun create(token: String): String {
        val session = UUID.randomUUID().toString()
        sessionsDao.save(token.hashText(), session)
        return session
    }
}
