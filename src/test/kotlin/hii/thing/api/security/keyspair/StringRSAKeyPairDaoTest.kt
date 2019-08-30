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

package hii.thing.api.security.keyspair

import hii.thing.api.security.keypair.dao.StringRSAKeyPairDao
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class StringRSAKeyPairDaoTest {

    val privatekey =
        """
-----BEGIN PRIVATE KEY-----
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
3am/BVWUtUSc+g/eQkGApvJYAxK0
-----END PRIVATE KEY-----
    """.trimIndent()

    val publicKey = """
-----BEGIN PUBLIC KEY-----
MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAsDH4So6CrQWGUFejlsZo
+adoxHD7VYEOLC7YnGKeers2kJZrKq1eqSQukLpBBb3uwY/QAu4t2rVDCOtC9SWg
i2YDRm0WBmPlsNpoGd9/rcf5B7CP7scnXw2CswkT5xq0vWouS9eRg6wSL6Nzrnwp
Z6L8zDyiRLdnfW41HPkSGO0fcCJ3pp1KOyG36gv9Yw8YIqxr7zB6mwhoZwmK9lAO
h9lJLvsRGlM7LiH401aJkX4SXulWLQJqnjybTrNcWMCKH20RxOKPYS/ZFQrhEc+A
dzloyxwhlQdICbTsQZ0LyTZJNeRuiPCZtQ0vrCR1p/OrFWTgRDEQLunxDoZ4KdS9
UQO2FnFWaS6E4icAySDHEOqbgnzlH+Wen9Aizlkx2CQO4qNeJ00iYf4IP9YqsIIO
s0oPeJtc954yodPFZvz9Lbp2esQ+qwUpNFHCvNNdZ1Mtj2phyCmlKA1OrGAYzepz
0k4CecWqQS8USLuTlZLI8v+hzHfFAzsijszS43xYKfJkIuXxNUGDlcavrB8U33S/
rMYM2SGA62x8uZr6rIXp5mCTfK1NgPQnCav2vgFmXVmPY+GTzr9uEd9ATGKNgcKr
ZUh8nJcMHPMDExDSJnMGONV0Xskx5mwqtkua2EpXBr6MT0+61kNdSADCCa3DxCPi
lvdrw6hFVnfEXui9JM8hg3cCAwEAAQ==
-----END PUBLIC KEY-----
    """.trimIndent()

    val dao = StringRSAKeyPairDao(privatekey, publicKey)
    @Test
    fun getPrivateKey() {
        dao.privateKey!!.algorithm `should be equal to` "RSA"
    }

    @Test
    fun getPublicKey() {
        dao.publicKey!!.algorithm `should be equal to` "RSA"
    }
}
