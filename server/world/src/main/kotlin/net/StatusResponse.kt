/*
 * Copyright 2018-2021 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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