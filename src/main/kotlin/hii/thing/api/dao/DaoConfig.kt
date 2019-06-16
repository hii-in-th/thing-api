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

package hii.thing.api.dao

// Postgres configuration
val pgUrl by lazy { System.getenv("PG_URL") }
val pgUsername by lazy { System.getenv("PG_USER") }
val pgPassword by lazy { System.getenv("PG_PASSWORD") }

// Redis configuration.
val redisHost by lazy { System.getenv("RE_HOST") }
val redisPort by lazy { System.getenv("RE_PORT").toInt() }
val redisExpireSec by lazy { System.getenv("RE_EXPIRE_SEC").toInt() }
