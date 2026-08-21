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

import io.ktor.client.request.get
import io.ktor.client.request.parameter

import com.acme.kmp.shared.Platform
import com.acme.kmp.shared.getPlatform
import com.acme.kmp.shared.utils.ApiClient
import com.acme.kmp.shared.utils.StateResult
import com.acme.kmp.shared.utils.createJsonParser

/** Utility to call Acme API Endpoints.
 * Ideally, this should be enclosed within a repository.
 * For now, keeping things simple.
 *
 * This is defined within `kmpShared` to any module can reuse it:
 * - `kmpCompose`: Applications can issue API calls, as required.
 * - `app-server`: Server components can issue API calls, if applicable.
 *   For this reference PoC, `app-server` will implement the `/acme/data` endpoint.
 */
object AcmeApiClient : ApiClient(
    host = getPlatform().apiHost,
    port = getPlatform().apiPort,
    json = createJsonParser(discriminator = ACME_DISCRIMINATOR, serializers = ACME_SERIALIZERS),
) {
    /** Error to process if API payload (response or error-response is invalid). */
    val defaultOnError = AcmeError(code = 0, message = "Error not provided by API")

    /** Calls endpoint [Platform.API_ACME_DATA] using `GET` method.
     *
     * @param delay Delay in seconds.
     * @param apiResult Result to return.
     */
    suspend fun getAcmeData(
        delay: Int,
        apiResult: AcmeApiResult,
    ): StateResult<AcmeData, AcmeError> =
        callApi(defaultOnError = defaultOnError) {
            get(Platform.API_ACME_DATA) {
                parameter("mode", apiResult)
                parameter("delay", delay)
            }
        }
}
