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

package hii.thing.api.device

import hii.thing.api.auth.AccessTokenManager
import hii.thing.api.auth.jwt.JwtAccessTokenManager
import hii.thing.api.device.dao.DeviceDao
import hii.thing.api.getDao
import hii.thing.api.security.JwtConst
import hii.thing.api.security.token.ThingPrincipal
import javax.annotation.security.RolesAllowed
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext

@Path("/device")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class DeviceResource(
    private val deviceDao: DeviceDao = getDao(),
    private val accessTokenManager: AccessTokenManager = JwtAccessTokenManager()
) {
    @Context
    lateinit var context: SecurityContext

    @POST
    @RolesAllowed("kiosk")
    fun createDevice(device: Device): Device {
        val userPrincipal = context.userPrincipal as ThingPrincipal
        val deviceKeyDetail = accessTokenManager.get(userPrincipal.accessToken)
        device.deviceId = JwtConst.decode(userPrincipal.accessToken).id
        device.type = deviceKeyDetail.roles.lastOrNull()

        return deviceDao.create(device)
    }

    @PUT
    @Path("/{deviceId:([\\w\\-]+)}")
    @RolesAllowed("kiosk")
    fun updateDevice(@PathParam("deviceId") deviceId: String, device: Device): Device {
        val userPrincipal = context.userPrincipal as ThingPrincipal
        val deviceKeyDetail = accessTokenManager.get(userPrincipal.accessToken)
        require(deviceKeyDetail.deviceID == deviceId) { "ค่า Device ID ที่ระบุไม่ตรงกับ Device ID ของ Token" }
        device.deviceId = deviceKeyDetail.deviceID
        device.type = deviceKeyDetail.roles.lastOrNull()

        return deviceDao.update(deviceId, device)
    }
}

data class Device(
    val location: String,
    var deviceId: String? = null,
    val deviceName: String,
    var type: String? = null
)
