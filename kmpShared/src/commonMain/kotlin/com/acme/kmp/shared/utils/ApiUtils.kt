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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

/** This should be considered a utility function to initialize a JSON parser.
 *
 * Creates a JSON parser similar to [io.ktor.serialization.kotlinx.json.DefaultJson].
 *
 * Includes [ContextualAnySerializer] to support `List<@Contextual Any>`
 * and `Map<String, @Contextual Any>` to decode/encode different collections.
 *
 * @param discriminator Name of the discriminator attribute.
 * @param serializers Map where the key is the same one as used in annotation `@SerialName`
 *   at the data class level.
 */
fun createJsonParser(
    discriminator: String,
    serializers: Map<String, KSerializer<out Any>>,
): Json =
    Json {
        // If a key is unknown, ignore it (backwards compatibility)
        ignoreUnknownKeys = true
        // If a key is optional and missing, use default value
        explicitNulls = false
        // Encode property defaults
        encodeDefaults = true
        isLenient = true
        /* Double.NaN -> encoded as NaN
         * Double.POSITIVE_INFINITY -> encoded as Infinity
         * Double.NEGATIVE_INFINITY -> encoded as -Infinity
         */
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = false
        useArrayPolymorphism = false
        serializersModule =
            SerializersModule {
                @Suppress("UNCHECKED_CAST")
                contextual(
                    kClass = Any::class,
                    serializer =
                        ContextualAnySerializer(
                            discriminator = discriminator,
                            serializers = serializers,
                        ) as KSerializer<Any>,
                )
            }
        // Use @SerialName at the data-class level to define the value for the discriminator attribute.
        classDiscriminator = discriminator
        @OptIn(ExperimentalSerializationApi::class)
        classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS
        // Development options
        prettyPrint = true
        allowComments = true
        allowTrailingComma = true
    }

/** Prettifies [Any] object into a `String`.
 * Similar to other JSON prettify functions.
 *
 * @param indentWidth Indentation width.
 */
fun Any?.toPrettyString(indentWidth: Int = 2): String {
    val toString = this.toString()
    val sb = StringBuilder()
    var indentLevel = 0
    val indent = " ".repeat(indentWidth)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                sb.append(char).append("\n")
                indentLevel++
                sb.append(indent.repeat(indentLevel))
            }
            ')', ']', '}' -> {
                sb.append("\n")
                indentLevel--
                sb.append(indent.repeat(indentLevel)).append(char)
            }
            ',' -> {
                sb.append(char).append("\n")
                sb.append(indent.repeat(indentLevel))
                if (i + 1 < toString.length && toString[i + 1] == ' ') {
                    i++ // Skip the original space after comma
                }
            }
            else -> sb.append(char)
        }
        i++
    }
    return sb.toString()
}
