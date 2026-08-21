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

import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.serializer

/** This serializer should be registered within [SerializersModule][kotlinx.serialization.modules.SerializersModule]
 * for data-models defining `Any` as the attribute data-type.
 * ```
 * val json = Json {
 *     serializersModule = SerializersModule {
 *         @Suppress("UNCHECKED_CAST")
 *         contextual(Any::class, ContextualAnySerializer as KSerializer<Any>)
 *         // Casting required to support `Any?`
 *     }
 * }
 * ```
 * Note that [createJsonParser] already includes it.
 *
 * This serializer is useful to support `List<@Contextual Any>` and `Map<String, @Contextual Any>`.
 * While this allows some JSON uncertainties, it will become useful when optimizing payloads
 * for Dynamic Content.
 *
 * **IMPORTANT**: This [KSerializer] only supports the following data-types:
 * - [Boolean]: `true`, `false`
 * - [Number]: `Int`, `Long`, `Float`, `Double`
 * - [String]: `String`
 * - [Pair]: `Pair<Any, Any>`
 * - [Iterable]: Any collection that implements [Iterable].
 * - [Array]: Any array.
 * - [Map]: `Map<String, Any>`
 * - Plus, any other [KSerializer] registered within
 *   [SerializersModule][kotlinx.serialization.modules.SerializersModule] in your JSON parser.
 *   For this module, see [createJsonParser].
 *
 * @property discriminator Name of the discriminator attribute.
 * @property serializers Map where the key is the same one as used in annotation `@SerialName`
 *   at the data class level.
 */
class ContextualAnySerializer(
    val discriminator: String,
    val serializers: Map<String, KSerializer<out Any>>,
) : KSerializer<Any?> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        ContextualSerializer(
            serializableClass = Any::class,
            fallbackSerializer = null,
            typeArgumentsSerializers = emptyArray(),
        ).descriptor

    override fun serialize(
        encoder: Encoder,
        value: Any?,
    ) {
        (encoder as? JsonEncoder)?.let {
            it.encodeJsonElement(value.encode(it))
        } ?: throw SerializationException("'ContextualAnySerializer' only supports JSON")
    }

    override fun deserialize(decoder: Decoder): Any =
        (decoder as? JsonDecoder)?.let {
            it.decodeJsonElement().decode(it)
        } ?: throw SerializationException("'ContextualAnySerializer' only supports JSON")

    /** Encodes [`Any?`][Any] into JSON recursively.
     * If a class is found, checks for contextual serializers, or, the related [`<class>.serializer()`][serializer].
     *
     * **IMPORTANT**: This [KSerializer] only supports the following data-types:
     * - [Boolean]: `true`, `false`
     * - [Number]: `Int`, `Long`, `Float`, `Double`
     * - [String]: `String`
     * - [Pair]: `Pair<Any, Any>`
     * - [Iterable]: Any collection that implements [Iterable].
     * - [Array]: Any array.
     * - [Map]: `Map<String, Any>`
     * - Plus, any other [KSerializer] registered within
     *   [SerializersModule][kotlinx.serialization.modules.SerializersModule] in your JSON parser.
     *   For this module, see [createJsonParser].
     */
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    private fun Any?.encode(encoder: JsonEncoder): JsonElement =
        when (this) {
            null -> JsonNull
            is JsonElement -> this
            is Boolean -> JsonPrimitive(this)
            is Number -> JsonPrimitive(this)
            is String -> JsonPrimitive(this)
            is Pair<*, *> -> JsonObject(mapOf(first.toString() to second.encode(encoder)))
            is Iterable<*> -> JsonArray(map { it.encode(encoder) })
            is Array<*> -> JsonArray(map { it.encode(encoder) })
            is Map<*, *> ->
                JsonObject(
                    content =
                        entries.associate { (k, v) ->
                            k.toString() to v.encode(encoder)
                        },
                )
            else -> {
                @Suppress("UNCHECKED_CAST")
                val serializer =
                    (encoder.serializersModule.getContextual(this::class) as? KSerializer<Any?>)
                        ?: (this::class.serializer() as? KSerializer<Any?>)
                        ?: throw SerializationException("Not supported type: ${this::class}")
                encoder.json.encodeToJsonElement(serializer, this)
            }
        }

    /** Decodes JSON recursively.
     * If a class is found, checks for contextual serializers, or, the related [`<class>.serializer()`][serializer].
     */
    @OptIn(ExperimentalSerializationApi::class)
    private fun JsonElement.decode(decoder: JsonDecoder): Any? =
        when (this) {
            is JsonNull -> null
            is JsonPrimitive ->
                when {
                    isString -> content
                    else -> booleanOrNull ?: intOrNull ?: longOrNull ?: floatOrNull ?: doubleOrNull ?: content
                }
            is JsonArray -> map { it.decode(decoder) }
            is JsonObject -> {
                this[discriminator]?.jsonPrimitive?.content?.let {
                    serializers[it]?.let { serializer ->
                        decoder.json.decodeFromJsonElement(serializer, this)
                    }
                } ?: entries.associate { (k, v) ->
                    k to v.decode(decoder)
                }
            }
        }.also {
            println("ktor -> json decoding: $it - ${if (it != null) it::class else "null"}")
        }
}
