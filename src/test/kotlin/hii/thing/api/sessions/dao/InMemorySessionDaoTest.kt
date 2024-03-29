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

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class InMemorySessionDaoTest {
    val sessionDao: SessionsDao = InMemorySessionDao()

    /* ktlint-disable max-line-length */
    val accessToken =
        """eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJhdXRoLmhpaS5pbi50aCIsImlhdCI6MTU1OTcxMzA2NSwiZXhwIjoxNTU5NzE0MjY4LCJhdWQiOiJ2aXRhbC5oaWkuaW4udGgiLCJzdWIiOiJkZXZpY2VzLzEwNTQ4NzExMSIsInJvbGUiOiJraW9zayIsImp0aSI6ImE4Y2E1ZGUyLTg3NTItMTFlOS1iYzQyLTUyNmFmNzc2NGY2NCJ9.D9L65_f4dpFMkOpzuguWpg0fZq2olSOLaYqTVNXzslPFgVaLst6oqkZYRmaWrsWxXxTG0orCqIboovn3jeHhmg""".trimMargin()
    /* ktlint-enable max-line-length */
    val session = "max-kios-cat-dog-x-cadot"

    @Test
    fun save() {
        sessionDao.save(accessToken, session)
    }

    @Test
    fun saveAndGet() {
        sessionDao.save(accessToken, session)

        sessionDao.get(accessToken) `should be equal to` session
    }
}
