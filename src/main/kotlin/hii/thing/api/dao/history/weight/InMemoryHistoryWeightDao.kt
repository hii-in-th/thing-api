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

package hii.thing.api.dao.history.weight

import hii.thing.api.vital.Weight
import java.util.LinkedList

class InMemoryHistoryWeightDao : HistoryWeightDao {
    private val storage = hashMapOf<String, LinkedList<Weight>>()

    override fun save(citizenId: String, weight: Weight): List<Weight> {
        try {
            storage[citizenId]!!.add(weight)
        } catch (ex: NullPointerException) {
            storage[citizenId] = LinkedList()
            storage[citizenId]!!.add(weight)
        }
        return get(citizenId)
    }

    override fun get(citizenId: String): List<Weight> {
        return storage[citizenId] ?: emptyList()
    }

    fun cleanAll() {
        storage.clear()
    }
}
