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

package hii.thing.api.sessions.dao.recordsession

import hii.thing.api.sessions.CreateSessionDetail

class InMemoryRecordSessionDao : RecordSessionDao {
    override fun register(sessionId: String, sessionDetail: CreateSessionDetail): CreateSessionDetail {
        require(store[sessionId] == null) { "Duplicate session id." }
        store[sessionId] = sessionDetail
        return sessionDetail
    }

    override fun update(sessionId: String, sessionDetail: CreateSessionDetail): CreateSessionDetail {
        require(store[sessionId] != null) { "Empty session update." }
        store[sessionId] = sessionDetail
        return sessionDetail
    }

    override fun getBy(citizenId: String): Map<String, CreateSessionDetail> {
        return store.filter {
            it.value.citizenId == citizenId
        }.takeIf { it.isNotEmpty() }!!
    }

    companion object {
        private val store = LinkedHashMap<String, CreateSessionDetail>()
    }

    override fun get(sessionId: String): CreateSessionDetail {
        require(store[sessionId] != null) { "Empty session update." }
        return store[sessionId]!!
    }

    fun cleanAll() {
        store.clear()
    }
}
