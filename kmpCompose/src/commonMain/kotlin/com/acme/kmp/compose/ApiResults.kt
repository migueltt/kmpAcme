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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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

import com.acme.kmp.shared.api.AcmeApiResult
import com.acme.kmp.shared.utils.StateResult
import com.acme.kmp.shared.utils.toPrettyString

/** This composable shows the API results section, allowing to:
 * - Define a delay for the related API endpoint - just to show a progress bar.
 * - Define the API result to be called.
 */
@Composable
fun ColumnScope.ApiResults(viewModel: AcmeViewModel) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var delay by rememberSaveable { mutableStateOf(0) }
    Text(
        text = "Delay: $delay seconds",
        fontSize = MaterialTheme.typography.titleLarge.fontSize,
        modifier = Modifier.align(Alignment.Start),
    )
    Slider(
        value = delay.toFloat(),
        onValueChange = {
            delay = it.toInt()
        },
        valueRange = 0f..10f,
        steps = 10,
    )
    val apiOptions = AcmeApiResult.entries
    var apiSelected by rememberSaveable { mutableStateOf(apiOptions[0]) }
    Text(
        text = "API result: $apiSelected",
        fontSize = MaterialTheme.typography.titleLarge.fontSize,
        modifier = Modifier.align(Alignment.Start),
    )
    Column {
        apiOptions.forEach { apiResult ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (apiResult == apiSelected),
                        onClick = { apiSelected = apiResult },
                        role = Role.RadioButton,
                    ).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (apiResult == apiSelected),
                    onClick = null, // Null here because row .selectable handles clicks
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
        onClick = {
            viewModel.getAcmeData(delay = delay, apiResult = apiSelected)
        },
    ) {
        Text("Send Request")
    }
    Text(
        text = "API Results",
        fontSize = MaterialTheme.typography.titleLarge.fontSize,
        modifier = Modifier.align(Alignment.Start),
    )
    when (uiState) {
        StateResult.Empty -> {
            Text(
                text = "No data",
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.Start),
                fontFamily = FontFamily.Monospace,
            )
        }
        StateResult.Processing -> {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        is StateResult.Failure -> {
            val textScrollState = rememberScrollState()
            Text(
                text = "${uiState.data.toPrettyString()}\n${uiState.error.toPrettyString()}",
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.Start).horizontalScroll(textScrollState),
                fontFamily = FontFamily.Monospace,
            )
        }
        is StateResult.Success -> {
            val textScrollState = rememberScrollState()
            Text(
                text = uiState.data.toPrettyString(),
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.Start).horizontalScroll(textScrollState),
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}
