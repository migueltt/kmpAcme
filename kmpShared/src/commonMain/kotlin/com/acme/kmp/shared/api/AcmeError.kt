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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Represents an error.
 *
 * This structure should be used by API endpoints in error-response-body,
 * in an effort to standardize error-handling.
 *
 * In a well-defined application, this could be used to channel errors through
 * different composables, displaying them in different ways.
 *
 * The data class is annotated with `@SerialName("AcmeError")` to be used by
 * [AcmeApiClient.json] as the key for the related [kotlinx.serialization.KSerializer].
 *
 * @property code Just an arbitrary error code.
 * @property message Just an arbitrary error message.
 */
@Serializable
@SerialName("AcmeError")
data class AcmeError(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String,
)
