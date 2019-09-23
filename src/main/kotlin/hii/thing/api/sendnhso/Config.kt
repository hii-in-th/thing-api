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

package hii.thing.api.sendnhso

internal val nhsoEndpoint by lazy { property("NHSO_ENDPOINT") }
internal val nhsoUsername by lazy { property("NHSO_USER") }
internal val nhsoPassword by lazy { property("NHSO_PASSWORD") }

internal data class LoginBody(val username: String, val password: String)
internal data class TokenBody(val token: String)

private fun property(keyName: String): String {
    System.getProperty(keyName)?.let { return it }
    return System.getenv(keyName) ?: "null"
}
