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

package hii.thing.api.sessions.dao

class InMemorySessionDao : SessionsDao {
    override fun save(token: String, session: String) {
        storage[token] = session
    }

    override fun get(token: String): String {
        return storage[token]!!
    }

    companion object {
        val storage = LinkedHashMap<String, String>()
    }

    override fun remove(token: String) {
        storage.remove(token)
    }
}
