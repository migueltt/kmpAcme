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

package com.acme.kmp.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass
import kotlinx.coroutines.Job

import com.acme.kmp.compose.utils.UiStateViewModel
import com.acme.kmp.shared.api.AcmeApiClient
import com.acme.kmp.shared.api.AcmeApiResult
import com.acme.kmp.shared.api.AcmeData
import com.acme.kmp.shared.api.AcmeError
import com.acme.kmp.shared.utils.StateResult

/** View-Model for [AcmeApp]. */
class AcmeViewModel : UiStateViewModel<AcmeData, AcmeError>(initial = StateResult.Empty) {
    /** Factory to use with [ViewModelProvider].
     * Required for some platforms.
     */
    companion object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(
            modelClass: KClass<T>,
            extras: CreationExtras,
        ): T {
            @Suppress("UNCHECKED_CAST")
            return AcmeViewModel() as T
        }
    }

    /** Triggers an API call using [AcmeApiClient].
     *
     * @param delay Delay in seconds.
     * @param apiResult Result to emit.
     */
    fun getAcmeData(
        delay: Int,
        apiResult: AcmeApiResult,
    ): Job =
        emitState {
            AcmeApiClient
                .getAcmeData(
                    delay = delay,
                    apiResult = apiResult,
                ).also {
                    if (it is StateResult.Success) {
                        println("ktor -> module: ${it.data.module}")
                    }
                }
        }
}
