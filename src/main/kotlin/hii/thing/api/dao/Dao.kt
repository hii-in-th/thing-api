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

/**
 * ใช้สำหรับในการคัดแยก class Dao
 * Dao(Data access object) ทั้งหมดต้อง implement interface Dao
 * @see Dao
 *
 * และหากต้องการใช้งาน getDao อย่าลืมสร้างชุดสร้างใน getDao() ด้วย
 * @see getDao
 */
interface Dao
