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

interface SendNHSO {
    /**
     * ส่งข้อมูลไปยัง nhso แล้วส่งสถานะสำเร็จกลับไปที่ ret
     * @param message ข้อมูลที่จะส่ง
     * @param autoMkdir ทำการสร้าง dir อัตโนมัติหรือไม่
     * @param ret callback ส่งสำเร็จ:true ไม่สำเร็จ:false
     */
    fun send(
        message: SendMessage,
        autoMkdir: Boolean = true,
        ret: (Boolean) -> Unit
    )
}
