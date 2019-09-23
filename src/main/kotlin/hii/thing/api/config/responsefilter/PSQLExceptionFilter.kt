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

package hii.thing.api.config.responsefilter

import org.postgresql.util.PSQLException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class PSQLExceptionFilter : ExceptionMapper<PSQLException> {
    override fun toResponse(exception: PSQLException): Response {
        val message = with(exception.message) {
            if (this != null && endsWith("insert or update on table \"session\" violates foreign key constraint")) {
                "Device นี้ยังไม่ได้ทำการลงทะเบียน จำเป็นต้องลงทะเบียนที่ /device ก่อน"
            } else
                exception.message
        }
        return ErrorDetail.build(WebApplicationException(message, exception, 400))
    }
}
