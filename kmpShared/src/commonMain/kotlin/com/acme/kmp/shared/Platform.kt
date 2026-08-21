/*
 *    Copyright 2026 migueltt and/or Contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.acme.kmp.shared

/** KMP platform abstraction. */
abstract class Platform {
    companion object {
        /** PoC API endpoint. */
        const val API_ACME_DATA: String = "/acme/data"
    }

    /** Platform name. */
    abstract val name: String

    /** Platform API hostname. */
    open val apiHost: String = "localhost"

    /** Platform API port. */
    open val apiPort: Int = 8080
}

/** Should provide the specific platform implementation. */
expect fun getPlatform(): Platform
