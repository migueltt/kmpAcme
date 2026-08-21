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

@file:OptIn(ExperimentalSerializationApi::class)

package com.acme.kmp.shared.api

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Sample Acme Data.
 *
 * This is just for this proof-of-concept and define proper guidelines and patterns
 * related to API payloads and how far we can introduce different JSON payloads.
 *
 * Note that [anyList] and [anyMap] should be considered antipatterns since they
 * such structures do not provide type-safety.
 *
 * The data class is annotated with `@SerialName("AcmeData")` to be used by
 * [AcmeApiClient.json] as the key for the related [kotlinx.serialization.KSerializer].
 *
 * @property platform Platform name.
 * @property timestamp Timestamp.
 * @property module Module info.
 * @property anyList A list of `@Contextual Any`. That is, it can specify types as defined in
 *   [com.acme.kmp.shared.utils.ContextualAnySerializer].
 * @property anyMap A map of `@Contextual Any`. That is, it can specify values with types as defined in
 *   [com.acme.kmp.shared.utils.ContextualAnySerializer].
 */
@Serializable
@SerialName("AcmeData")
data class AcmeData(
    @SerialName("platform")
    val platform: String,
    @SerialName("timestamp")
    val timestamp: LocalDateTime,
    @SerialName("module")
    val module: ModuleInfo,
    @SerialName("any_list")
    val anyList: List<@Contextual Any> = emptyList(),
    @SerialName("any_map")
    val anyMap: Map<String, @Contextual Any> = emptyMap(),
)
