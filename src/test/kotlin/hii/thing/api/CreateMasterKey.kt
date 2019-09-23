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

import hii.thing.api.auth.dao.devicekey.JwtDeviceKeyDao
import hii.thing.api.security.keypair.KeyPairManage
import hii.thing.api.security.keypair.dao.RSAKeyPairDao
import hii.thing.api.security.keypair.dao.StringRSAKeyPairDao
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * ใช้สำหรับสร้าง Master token
 * จำเป็นต้องใช้ private key และ public key ของ server
 * ไม่งั้น token จะไม่สามารถใช้งานได้ ตัว server มีกระบวนการ
 * ตรวจสอบ key
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
class CreateMasterKey {
    lateinit var jwtDeviceKeyDao: JwtDeviceKeyDao

    @Test
    fun createMasterKey() {
        println(jwtDeviceKeyDao.createMasterKey())
    }

    @Before
    fun setUp() {
        stringRsaKeyPari = StringRSAKeyPairDao(privatekey, publicKey)
        KeyPairManage.setUp(stringRsaKeyPari)
        jwtDeviceKeyDao = JwtDeviceKeyDao(KeyPairManage)
    }

    /* ktlint-disable max-line-length */
    val privatekey =
        """-----BEGIN PRIVATE KEY-----
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
-----END PRIVATE KEY-----
    """.trimIndent()

    val publicKey = """
-----BEGIN PUBLIC KEY-----
MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAsDH4So6CrQWGUFejlsZo+adoxHD7VYEOLC7YnGKeers2kJZrKq1eqSQukLpBBb3uwY/QAu4t2rVDCOtC9SWgi2YDRm0WBmPlsNpoGd9/rcf5B7CP7scnXw2CswkT5xq0vWouS9eRg6wSL6NzrnwpZ6L8zDyiRLdnfW41HPkSGO0fcCJ3pp1KOyG36gv9Yw8YIqxr7zB6mwhoZwmK9lAOh9lJLvsRGlM7LiH401aJkX4SXulWLQJqnjybTrNcWMCKH20RxOKPYS/ZFQrhEc+AdzloyxwhlQdICbTsQZ0LyTZJNeRuiPCZtQ0vrCR1p/OrFWTgRDEQLunxDoZ4KdS9UQO2FnFWaS6E4icAySDHEOqbgnzlH+Wen9Aizlkx2CQO4qNeJ00iYf4IP9YqsIIOs0oPeJtc954yodPFZvz9Lbp2esQ+qwUpNFHCvNNdZ1Mtj2phyCmlKA1OrGAYzepz0k4CecWqQS8USLuTlZLI8v+hzHfFAzsijszS43xYKfJkIuXxNUGDlcavrB8U33S/rMYM2SGA62x8uZr6rIXp5mCTfK1NgPQnCav2vgFmXVmPY+GTzr9uEd9ATGKNgcKrZUh8nJcMHPMDExDSJnMGONV0Xskx5mwqtkua2EpXBr6MT0+61kNdSADCCa3DxCPilvdrw6hFVnfEXui9JM8hg3cCAwEAAQ==
-----END PUBLIC KEY-----
    """.trimIndent()
    /* ktlint-enable max-line-length */
    lateinit var stringRsaKeyPari: RSAKeyPairDao
}
