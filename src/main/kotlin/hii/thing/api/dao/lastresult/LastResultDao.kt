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

import hii.thing.api.dao.Dao
import hii.thing.api.dao.refResultLinkLength
import hii.thing.api.vital.Result

/**
 * เก็บข้อมูลเกี่ยวกับการวัดครั้งล่า่สุด เพื่อที่จะไม่ต้อง query ที่ database ใหม่
 */
interface LastResultDao : Dao {
    /**
     * Clear all and set.
     * @return All result
     */
    fun set(citizenId: String, result: Result, refLink: String = GenUrl(refResultLinkLength).nextSecret()): Result

    fun get(citizenId: String): Result
    fun getBy(refLink: String): Result
    fun remove(citizenId: String)
}
