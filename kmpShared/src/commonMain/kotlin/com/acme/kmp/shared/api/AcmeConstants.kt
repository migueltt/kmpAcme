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

import kotlinx.serialization.KSerializer

import com.acme.kmp.shared.utils.ContextualAnySerializer

/** Defines serializers for [ContextualAnySerializer] for all the Data Models within this proof-of-concept. */
val ACME_SERIALIZERS: Map<String, KSerializer<out Any>> =
    mapOf(
        "AcmeData" to AcmeData.serializer(),
        "ModuleInfo" to ModuleInfo.serializer(),
        "AcmeError" to AcmeError.serializer(),
    )

/** Name of the discriminator attribute.
 * Used to include a special JSON attribute within all `@Serializable` classes.
 * Use `@SerialName("...")` at the data class level to define the value for the discriminator attribute.
 * This to provide support for polymorphic serialization/deserialization using [ContextualAnySerializer].
 */
const val ACME_DISCRIMINATOR: String = "clazz"
