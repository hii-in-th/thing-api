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

package hii.thing.api.vital.link

import com.auth0.jwt.JWT
import hii.thing.api.dao.keyspair.InMemoryRSAKeyPairDao
import hii.thing.api.security.keypair.KeyPairManage
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class JwtLinkTest {

    @Before
    fun setUp() {
        KeyPairManage.setUp(InMemoryRSAKeyPairDao())
    }

    val jwtLink = JwtLink()

    @Test
    fun create() {
        val link = jwtLink.create("abcdefg")
        JWT.decode(link).getClaim("ref").asString()!! `should be equal to` "abcdefg"
    }

    @Test
    fun getResult() {
        val link = jwtLink.create("abcdefg")
        jwtLink.getResult(link) `should be equal to` "abcdefg"
    }
}
