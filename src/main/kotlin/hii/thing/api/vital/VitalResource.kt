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

package hii.thing.api.vital

import hii.thing.api.dao.vital.bp.BloodPressuresDao
import hii.thing.api.dao.vital.bp.PgSqlBloodPressuresDao
import hii.thing.api.dao.vital.height.HeightsDao
import hii.thing.api.dao.vital.height.PgSqlHeightsDao
import hii.thing.api.dao.vital.weight.PgSqlWeightDao
import hii.thing.api.dao.vital.weight.WeightDao
import hii.thing.api.sessions.SessionsManager
import hii.thing.api.sessions.jwt.JwtSessionsManager
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

class VitalResource(
    val pbDao: BloodPressuresDao = PgSqlBloodPressuresDao(),
    // TODO heightsDao
    val heightsDao: HeightsDao = PgSqlHeightsDao(),
    val weightDao: WeightDao = PgSqlWeightDao(),
    val sessionsManager: SessionsManager = JwtSessionsManager()
) {

    @Context
    lateinit var context: SecurityContext

    @POST
    @Path("/blood-pressures")
    fun bpMeasure(bp: BloodPressures): BloodPressures {
        val accessToken = context.userPrincipal.name!!
        val session = sessionsManager.getBy(accessToken)
        bp.sessionId = session
        return pbDao.save(session, bp)
    }

    @POST
    @Path("/heights")
    fun heightsMeasure(height: Height): Height {
        val accessToken = context.userPrincipal.name!!
        val session = sessionsManager.getBy(accessToken)
        height.sessionId = session
        return heightsDao.save(session, height)
    }

    @POST
    @Path("/weight")
    fun weightMeasure(weight: Weight): Weight {
        val accessToken = context.userPrincipal.name!!
        val session = sessionsManager.getBy(accessToken)
        weight.sessionId = session
        return weightDao.save(session, weight)
    }
}
