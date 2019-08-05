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

import java.security.SecureRandom
import java.util.Locale
import java.util.Random

/**
 * ช่วยในการสร้าง reference link สำหรับเรียกคืนค่าผลการวัด
 * เพื่อนำไปใช้สำหรับสร้าง link เรียกคืนข้อมูลให้ user
 */
internal class GenUrl constructor(
    length: Int,
    val random: Random = SecureRandom(),
    val symbols: String = alphanum
) {
    private val buff: CharArray = CharArray(length)

    init {
        require(length > 0) { "secret's length must more than 0" }
        require(symbols.length > 1) { "require at least 2 symbols for generate secret" }
    }

    /**
     * Generate secret string
     */
    fun nextSecret(): String {
        for (i in buff.indices)
            buff[i] = symbols[random.nextInt(symbols.length)]
        return String(buff)
    }

    companion object {
        private const val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val lower = upper.toLowerCase(Locale.ROOT)
        private const val digits = "0123456789"
        private val alphanum = upper + lower + digits
    }
}
