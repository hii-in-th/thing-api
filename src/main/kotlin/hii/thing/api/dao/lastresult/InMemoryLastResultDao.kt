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

package hii.thing.api.dao.lastresult

private typealias Result = Map<String, String>
private typealias ResultStore = HashMap<String, String>

class InMemoryLastResultDao : LastResultDao {
    override fun set(citizenId: String, result: Result): Result {
        memory[citizenId] = HashMap(result)
        return result
    }

    override fun append(citizenId: String, result: Result): Result {
        return if (memory[citizenId] == null) set(citizenId, result)
        else {
            memory[citizenId]!!.putAll(result)
            memory[citizenId]!!
        }
    }

    override fun get(citizenId: String): Map<String, String> {
        return memory[citizenId]!!
    }

    override fun remove(citizenId: String) {
        require(memory[citizenId] != null)
        memory.remove(citizenId)
    }

    fun cleanAll() {
        memory.clear()
    }

    companion object {
        private val memory: HashMap<String, ResultStore> by lazy { hashMapOf<String, ResultStore>() }
    }
}
