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

package com.acme.kmp.shared.api

/** Should be used by UI composables to display which result is expected.
 * The enum itself should be passed as a query parameter.
 */
enum class AcmeApiResult(
    val label: String,
) {
    /** Selector to trigger a 200 OK response. */
    Success(label = "200"),

    /** Selector to trigger a 204 No-Content response. */
    NoData(label = "204"),

    /** Selector to trigger a 4XX Bad-Request response. */
    RequestFailure(label = "4XX"),

    /** Selector to trigger a 5XX Internal-Server-Error response. */
    ServerFailure(label = "5XX"),

    /** Selector to trigger a 5XX unknown response, with invalid error response body. */
    Unknown(label = "Unknown"),
}
