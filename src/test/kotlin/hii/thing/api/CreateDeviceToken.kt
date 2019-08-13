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

package hii.thing.api

import hii.thing.api.auth.Device
import hii.thing.api.dao.apikey.DeviceTokenDao
import hii.thing.api.dao.apikey.JwtDeviceTokenDao
import hii.thing.api.dao.keyspair.StringRSAKeyPairDao
import hii.thing.api.security.keypair.KeyPairManage
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * ใช้สำหรับสร้าง Device token
 * จำเป็นต้องใช้ private key และ public key ของ server
 * ไม่งั้น token จะไม่สามารถใช้งานได้ ตัว server มีกระบวนการ
 * ตรวจสอบ key มีขั้นตอนการใช้งานดังนี้
 * 1. กำหนดชื่อที่ deviceName
 * 2. สร้าง token โดยใช้
 * @see registerDevice
 *
 * สำหรับ Server สามารถสร้าง key โดยใช้คำสั่งตามนี้
 * $ openssl genrsa -out private_key.pem 4096
 * $ openssl rsa -pubout -in private_key.pem -out public_key.pem
 * $ openssl pkcs8 -topk8 -in private_key.pem -inform pem -out private_key_pkcs8.pem -outform pem -nocrypt
 *
 * นำข้อความใน private_key_pkcs8.pem มาใส่ในตัวแปร
 * @see privatekey
 *
 * นำข้อความใน public_key.pem มาใส่ในตัวแปร
 * @see publicKey
 */
@Ignore("สำหรับสร้าง Device token")
class CreateDeviceToken {
    val device = Device(
        deviceName = "dev/001",
        baseToken = "", // ไม่ต้องใส่
        roles = listOf("kiosk"),
        scope = listOf(
            "/auth"
        )
    )
    val deviceTokenDao: DeviceTokenDao = JwtDeviceTokenDao()

    /**
     * สำหรับสร้าง Device token
     */
    @Test
    fun registerDevice() {
        println(deviceTokenDao.registerDevice(device).baseToken)
    }

    @Before
    fun setUp() {
        KeyPairManage.setUp(stringRsaKeyPari)
    }

    val privatekey =
        """
-----BEGIN PRIVATE KEY-----
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
ds6qMg9icUmDBMSH/EWPZqbttImeDqHB
-----END PRIVATE KEY-----
    """.trimIndent()

    val publicKey = """
-----BEGIN PUBLIC KEY-----
MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAnA3VOT6qU/pbCKZZUuIV
ICOKtriC1vIncW0cdDqAFfQIFxLKuPr5nbXxG39eCKiIM4CwZVkZyGb+fQTaWGAJ
ZDKIOQN/COksMbOaX89ujBc1PeQ9t2GaZ5yjz4YTLfnQdDQ6aoqdfzbVrr9B9gqQ
gtAKa74DQsJD7gEU1jTcVLBEZW7uxlkafAOoQ7/Y2Y/k4SPDxf8ymY3yTV9VFHBm
XjR3N36Y45lszJOKvQ6zXHJ/Az7CSb+aM6azV8a6TiChScXjPkdQfhGvSZ9EopkQ
Tyq2m82H9+ePZQIpri9R9i4qasimHlfHJ5miDjz2IbUFpUWZnLYtJ/uWDGTH1Bzl
jF4PycAqruLpYR9Y+zVV/QvIWejCZnpzNWsFK3fpzMVgQUbKwsyL1QeXhdeNW2Qx
gzCs2G5wjp/9CcE3wA7GMCq3mzX445962z5IWtfT5SRRiNLevoqaE7L52xZwV3w7
NKTKrr54VqU6DDC04ASpxScaUO2UGA0OSfXrzI2dbRyVD/fU4TPJeONrdfdh/JOy
97N60+KoJ3pIviafHrUzTRG9H4Ejgq9uCIHch/5rS6UPinNmD2wz5145JdGOGo2v
COSDa+tn+tuEagTUNK2otGFxndV5OXlORGLL6AQJA49nqCtbmqp8nd5suKRq271M
ztV9vJGeL+Vmsk446IJbezsCAwEAAQ==
-----END PUBLIC KEY-----
    """.trimIndent()
    val stringRsaKeyPari = StringRSAKeyPairDao(privatekey, publicKey)
}
