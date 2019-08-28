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

package hii.thing.api.dao.history.height

import hii.thing.api.vital.Height
import java.util.LinkedList

class InmemoryHistoryHeightsDao : HistoryHeightsDao {
    private val storage = hashMapOf<String, LinkedList<Height>>()

    override fun save(citizenId: String, height: Height): List<Height> {
        try {
            storage[citizenId]!!.add(height)
        } catch (ex: NullPointerException) {
            storage[citizenId] = LinkedList()
            storage[citizenId]!!.add(height)
        }
        return get(citizenId)
    }

    override fun get(citizenId: String): List<Height> {
        return storage[citizenId] ?: emptyList()
    }

    fun cleanAll() {
        storage.clear()
    }
}
