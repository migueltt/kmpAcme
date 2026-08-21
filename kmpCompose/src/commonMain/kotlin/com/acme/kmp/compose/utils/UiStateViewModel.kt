package com.acme.kmp.compose.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.acme.kmp.shared.utils.StateResult

/** A View Model to streamline UI events.
 *
 * Results are reported through [StateResult].
 *
 * This is as simple as:
 * ```
 * class AcmeViewModel : UiStateViewModel<AcmeData, AcmeError>() {
 *    fun getAcmeData(delay: Int) {
 *        emitState {
 *            // call API returning AcmeData
 *        }
 *    }
 * }
 * ```
 *
 * - Concrete View Models should implement specific functions and call any of the
 * `emitXXX` functions.
 * - Composables should collect from the [uiState] flow.
 *   ```
 *   // Using delegate
 *   val uiState by viewModel.uiState.collectAsStateWithLifecycle()
 *   // or, to avoid issues with smartcasting.
 *   val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
 *   ```
 * - Within a composable, you should evaluate all the "states". For example:
 *   ```
 *   when (uiState) {
 *      // UI for empty state
 *      StateResult.Empty -> Text("No data")
 *      // UI for processing state
 *      StateResult.Processing -> LinearProgressIndicator()
 *      // UI for failure state
 *      is StateResult.Failure -> {
 *          // Use uiState.data (E) to access all the data and build UI
 *          // Access uiState.error for the related error/exception
 *      }
 *      // UI for success state
 *      is StateResult.Success -> {
 *          // Use uiState.data (T) to access all the data and build UI
 *      }
 *   }
 *   ```
 * Notice that this provides all the plumbing for simple patterns where on API call
 * provides all the information.
 *
 * Future patterns will include more complex ones.
 *
 * @param T Data type for success.
 * @param E Data type for failures.
 * @param initial Initial UI-state. Default: [StateResult.Processing].
 */
abstract class UiStateViewModel<T, E>(
    initial: StateResult<T, E> = StateResult.Processing,
) : ViewModel() {
    /** Internal UI-state - use `_uiState.emit(..)` to publish updates. */
    private val _uiState: MutableStateFlow<StateResult<T, E>> =
        MutableStateFlow(initial)

    // see https://youtu.be/fSB6_KE95bU?t=911
    // see https://developer.android.com/kotlin/flow

    /** Internal UI-state - use `_uiState.emit(..)` to publish updates. */
    val uiState: StateFlow<StateResult<T, E>> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initial,
        )

    /** Updates [uiState], based on the [block] return value.
     *
     * Concrete View-Model implementations should use this function when a suspend function is required.
     *
     * @param emitProcessing If `true`, it will emit [StateResult.Processing]. Default: `false`.
     * @param block Should return a new [StateResult]. Receives current value.
     *    This block must be fail-safe. You should catch any error/exception and return a [StateResult.Failure].
     */
    protected fun emitState(
        emitProcessing: Boolean = true,
        block: suspend (current: StateResult<T, E>) -> StateResult<T, E>,
    ): Job =
        viewModelScope.launch {
            val current = _uiState.value
            if (emitProcessing) {
                emitProcessing()
            }
            _uiState.emit(block.invoke(current))
        }

    /** Updates [uiState], based on the specified [value].
     *
     * @param value Value to emit.
     */
    protected suspend fun emitState(value: StateResult<T, E>) {
        _uiState.emit(value)
    }

    /** Updates [uiState], based on the specified [value].
     *
     * @param value Value to emit as a [StateResult.Success]. If `null`, then [StateResult.Empty] is emitted.
     */
    protected suspend fun emitState(value: T?) {
        if (value == null) {
            emitEmpty()
        } else {
            _uiState.emit(StateResult.Success(value))
        }
    }

    /** Emits [StateResult.Empty] state. */
    protected suspend fun emitEmpty() {
        _uiState.emit(StateResult.Empty)
    }

    /** Emits [StateResult.Processing] state. */
    protected suspend fun emitProcessing() {
        _uiState.emit(StateResult.Processing)
    }

    /** Updates [uiState], based on the specified throwable.
     *
     * @param e Any throwable.
     * @param data Failure payload.
     */
    protected suspend fun emitFailure(
        e: Throwable,
        data: E,
    ) {
        _uiState.emit(StateResult.Failure(error = e, data = data))
    }
}
