/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net

enum class StatusResponse(val opcode: Int) {
    SUCCESSFUL(0),
    DELAY(1),
    NORMAL(2),
    INVALID_CREDENTIALS(3),
    ACCOUNT_DISABLED(4),
    ACCOUNT_ONLINE(5),
    OUT_OF_DATE(6),
    SERVER_FULL(7),
    LOGIN_SERVER_OFFLINE(8),
    LOGIN_LIMIT_EXCEEDED(9),
    BAD_SESSION_ID(10),
    LOGIN_SERVER_REJECTED_SESSION(11),
    MEMBERS_ACCOUNT_REQUIRED(12),
    COULD_NOT_COMPLETE_LOGIN(13),
    SERVER_BEING_UPDATED(14),
    RECONNECTING(15),
    LOGIN_ATTEMPTS_EXCEEDED(16),
    MEMBERS_ONLY_AREA(17),
    INVALID_LOGIN_SERVER(20),
    TRANSFERRING_PROFILE(21);
}