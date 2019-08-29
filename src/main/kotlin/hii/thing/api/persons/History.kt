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

package hii.thing.api.persons

import hii.thing.api.dao.getDao
import hii.thing.api.dao.registerstore.RegisterStoreDao
import hii.thing.api.ignore
import java.time.LocalDateTime
import java.util.LinkedList

class History<T>(
    private val registerStoreDao: RegisterStoreDao = getDao(),
    private val getItem: (sessionId: String) -> Pair<T, LocalDateTime>
) {
    fun get(citizenId: String): List<T> {
        val itemList = LinkedList<Pair<T, LocalDateTime>>()
        registerStoreDao.getBy(citizenId).forEach { (session, _) ->
            val item = ignore { getItem(session) }
            if (item != null) itemList.addFirst(item)
        }
        itemList.sortWith(Comparator { o1, o2 ->
            when {
                o1.second.isAfter(o2.second) -> -1
                o1.second.isBefore(o2.second) -> 1
                else -> 0
            }
        })
        return itemList.map { it.first }
    }
}
