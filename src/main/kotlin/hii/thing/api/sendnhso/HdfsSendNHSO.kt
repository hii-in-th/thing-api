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

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.InlineDataPart
import com.github.kittinunf.fuel.core.Method
import hii.thing.api.config.GsonJerseyProvider
import hii.thing.api.config.toJson

class HdfsSendNHSO(endPoint: String = nhsoEndpoint) : SendNHSO {

    private val endPoint: String = if (endPoint.endsWith("/")) endPoint else "$endPoint/"

    companion object {
        private var accessToken: String = ""
    }

    override fun send(
        message: SendMessage,
        autoMkdir: Boolean,
        ret: (Boolean) -> Unit
    ) {
        Thread {
            val time = message.time
            val dirPath = "v1/nstda/kiosk/raw/${time.year}/${time.monthValue}/"
            val fileName = "${time.dayOfMonth}.csv"
            val csv = message.toCsv()

            append(dirPath, fileName, csv) {
                if (!it) { // ถ้า append ไม่สำเร็จ
                    if (autoMkdir) mkdir(dirPath)
                    val createStatus = createFile("$dirPath$fileName", csv)
                    ret(createStatus)
                } else
                    ret(true)
            }
        }.start()
    }

    private fun mkdir(dirPath: String) {
        val dirApi = "$endPoint$dirPath?op=MKDIRS"
        Fuel.put(dirApi) // Create dir.
            .header(authHeader)
            .response()
    }

    private fun createFile(firePath: String, message: String): Boolean {
        val createApi = "$endPoint$firePath?op=CREATE"
        val dataPart =
            InlineDataPart(content = message, name = "file", contentType = "multipart/form-data", filename = "file")
        val (_, response, _) = Fuel.upload(createApi, Method.PUT)
            .add(dataPart)
            .header(authHeader)
            .response()
        return response.statusCode == 201
    }

    private fun append(dirPath: String, fileName: String, message: String, ret: (Boolean) -> Unit) {
        val appendApi = "$endPoint$dirPath$fileName?op=APPEND"

        val dataPart =
            InlineDataPart(content = message, name = "file", contentType = "multipart/form-data", filename = "file")
        val (_, appendResponse, _) = Fuel.upload(appendApi)
            .add(dataPart)
            .header(authHeader)
            .response()

        val length = appendResponse.body().length
        val statusCode = appendResponse.statusCode
        if (statusCode == 200 && length!! == 0L) ret(true)
        else ret(false)

    }

    private val authHeader: Pair<String, String> = "Authorization" to "JWT $token"

    val token: String
        get() {
            if (tokenExpire) {
                val loginBody = LoginBody(nhsoUsername, nhsoPassword).toJson()
                val (_, response, _) = Fuel.post("${endPoint}auth-jwt")
                    .header("Content-Type" to "application/json")
                    .body(loginBody)
                    .response()
                check(response.statusCode == 200) { "Cannot 200 status is ${response.statusCode}" }
                val data = String(response.data)
                val tokenBody = GsonJerseyProvider.hiiGson.fromJson(data, TokenBody::class.java)!!
                accessToken = tokenBody.token
            }
            return accessToken
        }

    private val tokenExpire: Boolean
        get() {
            val tokenBody = TokenBody(accessToken).toJson()
            val (_, response, _) = Fuel.post("${endPoint}auth-jwt-verify")
                .header("Content-Type" to "application/json")
                .body(tokenBody)
                .response()

            return response.statusCode != 200
        }
}
