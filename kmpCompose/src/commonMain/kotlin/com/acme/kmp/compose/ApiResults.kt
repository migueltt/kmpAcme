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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.enums.EnumEntries

import com.acme.kmp.shared.api.AcmeApiModeParam
import com.acme.kmp.shared.api.AcmeData
import com.acme.kmp.shared.api.AcmeError
import com.acme.kmp.shared.utils.StateResult
import com.acme.kmp.shared.utils.toPrettyString

/** This composable shows the API results section, allowing to:
 * - Define a delay for the related API endpoint - just to show a progress bar.
 * - Define the API result to be called.
 * Adaptively lays out configuration controls and API result output based on screen width.
 */
@Composable
fun ApiResults(
    viewModel: AcmeViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var delay by rememberSaveable { mutableStateOf(1) }
    val apiMode: EnumEntries<AcmeApiModeParam> = AcmeApiModeParam.entries
    var apiSelected: AcmeApiModeParam by rememberSaveable { mutableStateOf(apiMode[0]) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isWide = maxWidth >= 600.dp
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    ApiSettingsControls(
                        delay = delay,
                        onDelayChange = { delay = it },
                        apiMode = apiMode,
                        apiSelected = apiSelected,
                        onApiSelectedChange = { apiSelected = it },
                        onRequest = {
                            viewModel.getAcmeData(delay = delay, apiResult = apiSelected).let { }
                        },
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    ApiResultsDisplay(uiState = uiState)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                ApiSettingsControls(
                    delay = delay,
                    onDelayChange = { delay = it },
                    apiMode = apiMode,
                    apiSelected = apiSelected,
                    onApiSelectedChange = { apiSelected = it },
                    onRequest = {
                        viewModel.getAcmeData(delay = delay, apiResult = apiSelected).let { }
                    },
                )
                Spacer(Modifier.height(16.dp))
                ApiResultsDisplay(uiState = uiState)
            }
        }
    }
}

/** Extension receiver overload for backward compatibility with [ColumnScope]. */
@Composable
@Suppress("UnusedReceiverParameter", "UnusedReceiver", "unused")
fun ColumnScope.ApiResults(
    viewModel: AcmeViewModel,
    modifier: Modifier = Modifier,
) {
    com.acme.kmp.compose.ApiResults(
        viewModel = viewModel,
        modifier = modifier,
    )
}

@Composable
private fun ApiSettingsControls(
    delay: Int,
    onDelayChange: (Int) -> Unit,
    apiMode: EnumEntries<AcmeApiModeParam>,
    apiSelected: AcmeApiModeParam,
    onApiSelectedChange: (AcmeApiModeParam) -> Unit,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Delay: $delay second(s)",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
        )
        Slider(
            value = delay.toFloat(),
            onValueChange = { onDelayChange(it.toInt()) },
            valueRange = 0f..10f,
            steps = 10,
        )
        Text(
            text = "API result: $apiSelected",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
        )
        Column {
            apiMode.forEach { apiResult ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (apiResult == apiSelected),
                            onClick = { onApiSelectedChange(apiResult) },
                            role = Role.RadioButton,
                        ).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (apiResult == apiSelected),
                        onClick = null,
                    )
                    Text(
                        text = apiResult.label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }
        }
        Button(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = onRequest,
        ) {
            Text("Send Request")
        }
    }
}

@Composable
private fun ApiResultsDisplay(
    uiState: StateResult<AcmeData, AcmeError>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "API Results",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
        )
        when (uiState) {
            StateResult.Empty -> {
                Text(
                    text = "No data",
                    modifier = Modifier.padding(vertical = 16.dp),
                    fontFamily = FontFamily.Monospace,
                )
            }
            StateResult.Processing -> {
                LinearProgressIndicator(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                )
            }
            is StateResult.Failure -> {
                val textScrollState = rememberScrollState()
                Text(
                    text = "${uiState.data.toPrettyString()}\n${uiState.error.toPrettyString()}",
                    modifier =
                        Modifier
                            .padding(vertical = 16.dp)
                            .horizontalScroll(textScrollState),
                    fontFamily = FontFamily.Monospace,
                )
            }
            is StateResult.Success -> {
                val textScrollState = rememberScrollState()
                Text(
                    text = uiState.data.toPrettyString(),
                    modifier =
                        Modifier
                            .padding(vertical = 16.dp)
                            .horizontalScroll(textScrollState),
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}
