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

package hii.thing.api.security.keypair.dao

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class StringRSAKeyPairDao(privateKeyString: String, publicKeyString: String) :
    RSAKeyPairDao {
    private lateinit var pubKey: RSAPublicKey
    private lateinit var priKey: RSAPrivateKey

    init {
        require(privateKeyString != "null")
        require(publicKeyString != "null")
        val kf = KeyFactory.getInstance("RSA")
        runBlocking {
            launch {
                val privateKeyContent =
                    privateKeyString.replace(Regex("\n"), "").replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent))
                priKey = kf.generatePrivate(keySpecPKCS8) as RSAPrivateKey
            }
            launch {
                val publicKeyContent =
                    publicKeyString.replace(Regex("\n"), "").replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                val keySpecX509 = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent))
                pubKey = kf.generatePublic(keySpecX509) as RSAPublicKey
            }
        }
    }

    override var privateKey: RSAPrivateKey?
        get() = priKey
        set(value) {
        }
    override var publicKey: RSAPublicKey?
        get() = pubKey
        set(value) {
        }
}
