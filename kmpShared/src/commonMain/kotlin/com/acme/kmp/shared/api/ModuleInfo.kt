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

/** Module Information.
 *
 * Should be based on the generated `ModuleBuildConfig` for each module.
 *
 * The data class is annotated with `@SerialName("ModuleInfo")` to be used by
 * [AcmeApiClient.json] as the key for the related [kotlinx.serialization.KSerializer].
 *
 * @property id Module identifier.
 * @property name Module name.
 * @property group Module group.
 * @property version Module version.
 */
@Serializable
@SerialName("ModuleInfo")
data class ModuleInfo(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("group")
    val group: String,
    @SerialName("version")
    val version: String,
)
