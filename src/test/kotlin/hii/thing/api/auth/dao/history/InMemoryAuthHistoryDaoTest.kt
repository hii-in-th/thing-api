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

package hii.thing.api.auth.dao.history

import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class InMemoryAuthHistoryDaoTest {
    private val storage = InMemoryDeviceIdMapToAccessId()
    val dao: DeviceIdMapToAccessId = storage

    private val deviceId = "123-324-234"
    private val issuedTo = "9292-43543-435-43"
    private val deviceId2 = "123-324-8743"
    private val issuedTo2 = "9292-43543-435-34"

    @Test
    fun recordAndGet() {
        dao.record(deviceId, issuedTo)
        dao.getDeviceIdBy(issuedTo) `should be equal to` deviceId
    }

    @Test
    fun recordAndMultiGet() {
        dao.record(deviceId, issuedTo)
        dao.record(deviceId2, issuedTo2)
        dao.getDeviceIdBy(issuedTo) `should be equal to` deviceId
        dao.getDeviceIdBy(issuedTo2) `should be equal to` deviceId2
    }

    @Before
    fun setUp() {
        storage.clean()
    }
}
