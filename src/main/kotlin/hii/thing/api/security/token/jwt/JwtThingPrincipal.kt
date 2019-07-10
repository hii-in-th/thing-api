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

package hii.thing.api.security.token.jwt

import hii.thing.api.security.JwtConst
import hii.thing.api.security.token.ThingPrincipal

class JwtThingPrincipal(override val accessToken: String) : ThingPrincipal {
    override fun getName(): String = jwt.subject
    override fun getRole(): Array<String> = jwt.getClaim("role").asArray(String::class.java)
    override val deviceName: String get() = jwt.subject
    val jwt = JwtConst.decode(accessToken)
}
