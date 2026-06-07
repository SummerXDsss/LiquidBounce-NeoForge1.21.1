/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2024 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 *
 */
package net.ccbluex.liquidbounce.web.socket.protocol.rest.client

import com.google.gson.JsonObject
import net.ccbluex.liquidbounce.api.IpInfoApi
import net.ccbluex.liquidbounce.api.ClientApi
import net.ccbluex.liquidbounce.utils.client.isPremium
import net.ccbluex.liquidbounce.utils.client.mc
import net.ccbluex.liquidbounce.web.socket.netty.httpForbidden
import net.ccbluex.liquidbounce.web.socket.netty.httpOk
import net.ccbluex.liquidbounce.web.socket.netty.rest.RestNode
import net.ccbluex.liquidbounce.web.socket.protocol.protocolGson

internal fun sessionJson() = JsonObject().apply {
    val session = mc.session
    val username = session.username.takeIf { it.isNotBlank() } ?: "Player"
    val uuid = runCatching { session.uuidOrNull }.getOrNull()

    addProperty("username", username)
    addProperty("uuid", uuid?.toString().orEmpty())
    addProperty(
        "accountType",
        runCatching { session.accountType.getName() }.getOrElse { session.accountType.name.lowercase() }
    )
    addProperty("avatar", ClientApi.formatAvatarUrl(uuid, username))
    addProperty("premium", session.isPremium())
}

fun RestNode.sessionRest() {
    get("/session") {
        httpOk(sessionJson())
    }
    get("/location") {
        httpOk(
            protocolGson.toJsonTree(
                IpInfoApi.localIpInfo ?: return@get httpForbidden("location is not known (yet)")
            )
        )
    }
}
