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

/** Represents a state result from a repository.
 *
 * Similar to [Result], but adds 2 more states: [Processing], [Empty].
 *
 * Note that this result could be coming from:
 * - An API call
 * - Room Database
 * - Function
 * - Any arbitrary instance creation
 *
 * Additionally, defines [Failure]s to include the related exception plus
 * an arbitrary object, describing the error.
 *
 * TODO: May be replaced with [Rich-Errors](https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0441-rich-errors-motivation.md)
 *   and [KotlinConf2025](https://resources.jetbrains.com/storage/products/kotlinconf-2025/may-22/Rich%20Errors%20in%20Kotlin%20_%20Michail%20Zarečenskij.pdf)
 *
 * @param T Success payload.
 * @param E Failure payload.
 */
sealed class StateResult<out T, out E> {
    /** Represents the "processing" state. */
    data object Processing : StateResult<Nothing, Nothing>()

    /** Represents a success with payload [T].
     *
     * @param T Type for [data].
     * @property data Success payload.
     */
    data class Success<out T>(
        val data: T,
    ) : StateResult<T, Nothing>()

    /** Represents a success without any payload. */
    data object Empty : StateResult<Nothing, Nothing>()

    /** Represents a failure.
     *
     * @param E Type for [data].
     * @property error Exception.
     * @property data Failure payload.
     */
    data class Failure<out E>(
        val error: Throwable,
        val data: E,
    ) : StateResult<Nothing, E>()
}
