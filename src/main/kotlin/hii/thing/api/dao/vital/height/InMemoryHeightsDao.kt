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

package hii.thing.api.dao.vital.height

import hii.thing.api.vital.Height

class InMemoryHeightsDao : HeightsDao {
    override fun save(session: String, height: Height): Height {
        require(store[session] == null)
        store[session] = Height(height.height, session, height.time)
        return store[session]!!
    }

    override fun getBy(session: String): Height {
        val height = store[session]
        require(height != null)
        return height
    }

    companion object {
        private val store = LinkedHashMap<String, Height>()
    }

    fun cleanAll() {
        store.clear()
    }
}
