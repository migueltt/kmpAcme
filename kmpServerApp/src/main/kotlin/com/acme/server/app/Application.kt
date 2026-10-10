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

package com.acme.server.app

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

import com.acme.kmp.shared.Platform
import com.acme.kmp.shared.api.ACME_DISCRIMINATOR
import com.acme.kmp.shared.api.ACME_SERIALIZERS
import com.acme.kmp.shared.api.AcmeApiModeParam
import com.acme.kmp.shared.api.AcmeData
import com.acme.kmp.shared.api.AcmeError
import com.acme.kmp.shared.api.ModuleInfo
import com.acme.kmp.shared.getPlatform
import com.acme.kmp.shared.utils.createJsonParser

/** Starts the `app-server` microservice. */
fun main() {
    embeddedServer(
        factory = Netty,
        port = getPlatform().apiPort,
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

/** Ktor Server module.
 *
 * Registers `GET /acme/data` endpoint.
 *
 * @receiver the `app-server` microservice.
 */
fun Application.module() {
    // Required if the API endpoints are listening in a different port - mainly for Web apps.
    install(CORS) {
        // For development purposes, you could allow any host
        anyHost()
        // Or, restrict to specific port based on how js/wasm are launched
        // allowHost("localhost:8082")
        // Allow specific headers, methods
        // allowHeader(HttpHeaders.ContentType)
        // allowMethod(HttpMethod.Get)
        // allowMethod(HttpMethod.Post)
        // :
    }
    // Require for JSON data interchange.
    install(ContentNegotiation) {
        json(json = createJsonParser(discriminator = ACME_DISCRIMINATOR, serializers = ACME_SERIALIZERS))
    }
    // Routing for API endpoints.
    routing {
        // GET /acme/data demo endpoint.
        get(Platform.API_ACME_DATA) {
            call.request.queryParameters["delay"]?.toIntOrNull()?.let {
                println("Delaying response by $it seconds...")
                delay(it.seconds)
            }
            println("Processing response")
            val mode =
                call.request.queryParameters["mode"].let { mode ->
                    when (mode) {
                        null -> AcmeApiModeParam.Unknown
                        else ->
                            try {
                                AcmeApiModeParam.valueOf(mode)
                            } catch (_: IllegalArgumentException) {
                                // Invalid "mode" parameter.
                                call.respond(
                                    status = HttpStatusCode.BadRequest,
                                    message =
                                        AcmeError(
                                            code = HttpStatusCode.BadRequest.value,
                                            message = "Invalid 'mode' parameter",
                                        ),
                                )
                                return@get
                            }
                    }
                }
            when (mode) {
                AcmeApiModeParam.Success ->
                    // Send back success
                    call.respond(
                        AcmeData(
                            platform = getPlatform().name,
                            timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                            module =
                                ModuleInfo(
                                    id = ModuleBuildConfig.MODULE_ID,
                                    name = ModuleBuildConfig.MODULE_NAME,
                                    group = ModuleBuildConfig.MODULE_GROUP,
                                    version = ModuleBuildConfig.MODULE_VERSION,
                                ),
                            // Include a list to demonstrate polymorphic serialization.
                            anyList = createAnyList(),
                            // Include a map to demonstrate polymorphic serialization.
                            anyMap = createAnyMap(),
                        ),
                    )
                AcmeApiModeParam.NoData ->
                    // 204 No content
                    call.respond(HttpStatusCode.NoContent)
                AcmeApiModeParam.RequestFailure ->
                    // 400 Bad request with a standard error response body
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message =
                            AcmeError(
                                code = HttpStatusCode.BadRequest.value,
                                message = "Bad Request",
                            ),
                    )
                AcmeApiModeParam.ServerFailure ->
                    // 500 Internal Server Error with a standard error response body
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message =
                            AcmeError(
                                code = HttpStatusCode.InternalServerError.value,
                                message = "Internal Server Error",
                            ),
                    )
                AcmeApiModeParam.Unknown ->
                    // 500 Internal Server Error but with an invalid response body payload
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "invalid-payload",
                    )
            }
        }
    }
}

/** A simple data class without `@SerialName` annotation.
 * This will cause to include `"clazz":"com.acme.server.app.TestData"` in the JSON payload.
 * Since it is NOT registered within [ACME_SERIALIZERS], it will be deserialized as a map.
 */
@Serializable
data class TestData(
    val integer: Int,
    val text: String,
    val date: LocalDate,
    val timestamp: LocalDateTime,
)

/** Note that this is not recommended since requires polymorphic adapter to encode/decode JSON.
 *
 * @see com.acme.kmp.shared.utils.ContextualAnySerializer
 */
private fun createAnyList(): List<Any> =
    buildList {
        add("String value")
        add(123.45)
        add(999)
        add(true)
        // Since it implements Serializable, it will use the 'serializer'.
        add(Pair("key1", "value1"))
        // kotlinx.datetime classes register their serializers.
        add(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
        add(
            // Since it implements Serializable, it will use the 'serializer'.
            AcmeData(
                platform = getPlatform().name,
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                module =
                    ModuleInfo(
                        id = ModuleBuildConfig.MODULE_ID,
                        name = ModuleBuildConfig.MODULE_NAME,
                        group = ModuleBuildConfig.MODULE_GROUP,
                        version = ModuleBuildConfig.MODULE_VERSION,
                    ),
            ),
        )
        add(
            // Since it implements Serializable, it will use the 'serializer'.
            AcmeError(
                code = 1,
                message = "Test error",
            ),
        )
        add(
            // Raw JSON but includes `clazz=ModuleInfo`.
            // Thus, it will be serialized as a JsonObject,
            // but deserialized as data class `ModuleInfo` on the client.
            JsonObject(
                mapOf(
                    "clazz" to JsonPrimitive("ModuleInfo"),
                    "id" to JsonPrimitive("test-id"),
                    "name" to JsonPrimitive("test-name"),
                    "group" to JsonPrimitive("test-group"),
                    "version" to JsonPrimitive("test-version"),
                ),
            ),
        )
        add(
            // Since it implements Serializable, it will use the 'serializer'.
            TestData(
                integer = 123,
                text = "String value in list",
                date = LocalDate(2023, 1, 1),
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            ),
        )
        add(createAnyMap())
    }

/** Note that this is not recommended since requires polymorphic adapter to encode/decode JSON.
 *
 * @see com.acme.kmp.shared.utils.ContextualAnySerializer
 */
private fun createAnyMap(): Map<String, Any> =
    buildMap {
        this["string"] = "String value"
        this["double"] = 123.45
        this["integer"] = 999
        this["boolean"] = true
        this["pair"] = Pair("key1", "value1")
        // kotlinx.datetime classes register their serializers.
        this["timestamp"] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        this["acmeData"] =
            // Since it implements Serializable, it will use the 'serializer'.
            AcmeData(
                platform = getPlatform().name,
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                module =
                    ModuleInfo(
                        id = ModuleBuildConfig.MODULE_ID,
                        name = ModuleBuildConfig.MODULE_NAME,
                        group = ModuleBuildConfig.MODULE_GROUP,
                        version = ModuleBuildConfig.MODULE_VERSION,
                    ),
            )
        this["acmeError"] =
            // Since it implements Serializable, it will use the 'serializer'.
            AcmeError(
                code = 1,
                message = "Test error",
            )
        this["test-module-info"] =
            // Raw JSON but includes `clazz=ModuleInfo`.
            // Thus, it will be serialized as a JsonObject,
            // but deserialized as data class `ModuleInfo` on the client.
            JsonObject(
                mapOf(
                    "clazz" to JsonPrimitive("ModuleInfo"),
                    "id" to JsonPrimitive("test-id"),
                    "name" to JsonPrimitive("test-name"),
                    "group" to JsonPrimitive("test-group"),
                    "version" to JsonPrimitive("test-version"),
                ),
            )
        this["testData"] =
            // Since it implements Serializable, it will use the 'serializer'.
            TestData(
                integer = 9909,
                text = "String value in map",
                date = LocalDate(2023, 2, 14),
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            )
    }
