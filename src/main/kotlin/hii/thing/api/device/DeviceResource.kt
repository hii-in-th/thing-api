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
import hii.thing.api.auth.DeviceKeyDetail
import hii.thing.api.auth.dao.devicekey.DeviceKeyDao
import hii.thing.api.auth.jwt.JwtAccessTokenManager
import hii.thing.api.device.dao.DeviceDao
import hii.thing.api.getDao
import hii.thing.api.getLogger
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
    private val deviceKeyDao: DeviceKeyDao = getDao(),
    private val accessTokenManager: AccessTokenManager = JwtAccessTokenManager()
) {
    @Context
    lateinit var context: SecurityContext

    /**
     * สร้าง device แล้วส่งค่ากลับเป็น key
     */
    @POST
    @RolesAllowed("master")
    fun register(device: Device): DeviceToken {
        //  สร้าง key
        val deviceKeyDetail = deviceKeyDao.registerDevice(
            DeviceKeyDetail(
                "${device.type}/${device.deviceName}",
                "",
                listOf("kiosk", device.type),
                listOf("/auth")
            )
        )
        device.deviceId = deviceKeyDetail.deviceID
        deviceDao.create(device) // สร้างข้อมูล device
        logger.info { "Create device token name ${deviceKeyDetail.deviceName}" }
        return DeviceToken(deviceKeyDetail.deviceKey)
    }

    @PUT
    @Path("/{deviceId:([\\w\\-]+)}")
    @RolesAllowed("kiosk")
    fun updateDevice(@PathParam("deviceId") deviceId: String, device: Device): Device {
        val userPrincipal = context.userPrincipal as ThingPrincipal
        val deviceKeyDetail = accessTokenManager.get(userPrincipal.accessToken)
        require(deviceKeyDetail.deviceID == deviceId) { "ค่า Device ID ที่ระบุไม่ตรงกับ Device ID ของ Token" }
        device.deviceId = deviceKeyDetail.deviceID
        device.type = deviceKeyDetail.roles.lastOrNull()!!

        return deviceDao.update(deviceId, device)
    }

    companion object {
        val logger by lazy { getLogger() }
    }
}

data class Device(
    val deviceName: String, // require create
    val location: String, // require create
    var type: String, // group
    var deviceId: String? = null
)

data class DeviceToken(val deviceToken: String)
