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

import hii.thing.api.dao.refResultLinkLength
import org.jetbrains.exposed.sql.Table

object SqlLastResult : Table("tmp_last_result") {
    val citizenId = varchar("citizen", 30).primaryKey(0)
    val refLink = varchar("ref_link", refResultLinkLength).index("idx", true)
    val value = varchar("value", 255)
    val updateTime = datetime("update")
}
