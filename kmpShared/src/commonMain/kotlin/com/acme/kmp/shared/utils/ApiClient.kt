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

package com.acme.kmp.shared.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.json.Json

/** API Client to use across modules for consistency.
 *
 * Concrete implementations should add functions for each API endpoint.
 *
 * @property host Host to connect to.
 * @property port Port to connect to.
 * @property json JSON parser.
 */
abstract class ApiClient(
    val host: String,
    val port: Int,
    val json: Json,
) {
    /** HTTP client to use for API requests. */
    protected val httpClient by lazy {
        // WARNING: Do not specify engine - each platform will be assigned accordingly.
        HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
            defaultRequest {
                host = this@ApiClient.host
                port = this@ApiClient.port
            }
            // Forces to throw ClientRequestException (4XX), ServerRequestException (5XX)
            expectSuccess = true
        }
    }

    /** Handles errors from API calls.
     *
     * Note that this is `inline` - thus, copied at the caller's site.
     * Notice that this relies on the other version which includes `TypeInfo` parameters,
     * decreasing the amount of code that is actually inlined.
     *
     * Note that [`expectSuccess`][io.ktor.client.HttpClientConfig.expectSuccess] must be `true`.
     * - [StateResult.Success] is returned if the call is successful.
     * - [StateResult.Failure] is returned if the call fails, either for 4XX or 5XX.
     *
     * @param T Expected type of the response to wrap within [StateResult].
     * @param E Expected type of the response-error to wrap within [StateResult].
     * @param defaultOnError Error to use if not provided by API, or the provided error-response-body is invalid.
     * @param block API call block.
     */
    protected suspend inline fun <reified T : Any, reified E : Any> callApi(
        defaultOnError: E,
        noinline block: suspend HttpClient.() -> HttpResponse,
    ): StateResult<T, E> =
        callApi(
            tTypeInfo = typeInfo<T>(),
            eTypeInfo = typeInfo<E>(),
            defaultOnError = defaultOnError,
            block = block,
        )

    /** Handles errors from API calls.
     *
     * Similar as the other version, but it is not `inline`.
     * Thus, the amount of code generated is smaller.
     *
     * Note that [`expectSuccess`][io.ktor.client.HttpClientConfig.expectSuccess] must be `true`.
     * - [StateResult.Success] is returned if the call is successful.
     * - [StateResult.Failure] is returned if the call fails, either for 4XX or 5XX.
     *
     * @param T Expected type of the response to wrap within [StateResult].
     * @param E Expected type of the response-error to wrap within [StateResult].
     * @param tTypeInfo TypeInfo for [T].
     * @param eTypeInfo TypeInfo for [E].
     * @param defaultOnError Error to use if not provided by API, or the provided error-response-body is invalid.
     * @param block API call block.
     */
    protected suspend fun <T : Any, E : Any> callApi(
        tTypeInfo: TypeInfo,
        eTypeInfo: TypeInfo,
        defaultOnError: E,
        block: suspend HttpClient.() -> HttpResponse,
    ): StateResult<T, E> =
        try {
            val response = block.invoke(httpClient)
            when (response.status) {
                HttpStatusCode.NoContent -> StateResult.Empty
                else -> StateResult.Success(response.body(tTypeInfo))
            }
        } catch (e: ResponseException) {
            StateResult.Failure(
                error = e,
                data =
                    try {
                        e.response.body(eTypeInfo)
                    } catch (_: Throwable) {
                        defaultOnError
                    },
            )
        } catch (e: Throwable) {
            StateResult.Failure(
                error = e,
                data = defaultOnError,
            )
        }
}
