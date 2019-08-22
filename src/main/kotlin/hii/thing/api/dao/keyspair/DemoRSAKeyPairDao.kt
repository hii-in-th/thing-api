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

package hii.thing.api.dao.keyspair

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class DemoRSAKeyPairDao : RSAKeyPairDao {
    private val privateStringKey = """
-----BEGIN PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAzS6sJSTx16Wg+J7u
acLD8CI9E9Q7NS4jb7nVGpjuqhCpRAxlosvGhcHOuEqsFLwYosh10+L9rsgDJTnK
IsCZlwIDAQABAkBVluSEUhb4B8VQMKvvPimR4BZuUReppWdu0awNfkIAelavCl0c
f2vgi5SKNe2mbem0YxWipzS++m/VK/w5I8ehAiEA/AbMo1gcNzJ2OkekuxSKQdLk
7s7nnlsbV+Ju4jqFV/kCIQDQas5DRFTypJnMdX6YF9pWmAhw7BpC5S8WNFEx6WSC
DwIhAKhonrwT957GwIwHLcO5YP3FQCd36Pw/YxEiBPh1JH95AiAmGE+pVfUdAN8n
0xYXoMyE5XcthMpsDc4khd2NfPalGwIhAPFYFKuKAfslHjvRY3uVehhrM+cvI4iY
+/ivJk0ve0Cs
-----END PRIVATE KEY-----
    """.trimIndent()
    private val publicStringKey = """
-----BEGIN PUBLIC KEY-----
MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAM0urCUk8deloPie7mnCw/AiPRPUOzUu
I2+51RqY7qoQqUQMZaLLxoXBzrhKrBS8GKLIddPi/a7IAyU5yiLAmZcCAwEAAQ==
-----END PUBLIC KEY-----
    """.trimIndent()
    private val dao = StringRSAKeyPairDao(privateStringKey, publicStringKey)

    override var privateKey: RSAPrivateKey?
        get() = dao.privateKey
        set(value) {}
    override var publicKey: RSAPublicKey?
        get() = dao.publicKey
        set(value) {}
}
