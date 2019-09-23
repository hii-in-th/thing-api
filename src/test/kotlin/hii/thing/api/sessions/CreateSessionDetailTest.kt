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

package hii.thing.api.sessions

import org.amshove.kluent.`should equal`
import org.junit.Test
import java.time.LocalDateTime

class CreateSessionDetailTest {

    @Test
    fun birthDateToAge() {
        val detail = CreateSessionDetail("")
        val age = detail.birthDateToAge("1990-01-16", LocalDateTime.of(2019, 8, 14, 22, 22))

        age `should equal` 29
    }
}
