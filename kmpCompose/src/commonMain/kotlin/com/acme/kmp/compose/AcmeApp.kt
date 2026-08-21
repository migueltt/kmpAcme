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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource

import com.acme.kmp.compose.kmpcompose.generated.resources.Res
import com.acme.kmp.compose.kmpcompose.generated.resources.compose_multiplatform

import com.acme.kmp.compose.theme.AppTheme
import com.acme.kmp.compose.theme.Theme
import com.acme.kmp.shared.Greeting

/** Main composable application.
 *
 * @param colorScheme Color scheme. Defaults to either [Theme.darkScheme] or [Theme.lightScheme].
 * @param viewModel ViewModel.
 */
@Composable
fun AcmeApp(
    colorScheme: ColorScheme = if (isSystemInDarkTheme()) Theme.darkScheme else Theme.lightScheme,
    viewModel: AcmeViewModel = viewModel<AcmeViewModel>(factory = AcmeViewModel),
) {
    AppTheme(colorScheme = colorScheme) {
        // Restore state after recomposition.
        var showGreeting by rememberSaveable { mutableStateOf(false) }
        var showApiResults by rememberSaveable { mutableStateOf(false) }
        val greeting = remember { Greeting().greet() }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val columnScrollState = rememberScrollState()
            Column(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(columnScrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(32.dp))
                Button(onClick = { showGreeting = !showGreeting }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showGreeting) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 16.dp))
                Button(onClick = { showApiResults = !showApiResults }) {
                    Text("Call API")
                }
                AnimatedVisibility(showApiResults) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ApiResults(viewModel)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------------------------------

/* Previews should be included only in this `kmpCompose` module.
 *
 * Note that within `kmpCompose/build.gradle.kts` these 2 dependencies must be included:
 * implementation(libs.compose.ui.tooling.preview) -> already included through `libs.bundles.compose.multiplatform`
 * "androidRuntimeClasspath"(libs.compose.ui.tooling)
 */

@Composable
@Preview(
    showBackground = true,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_NO,
    showSystemUi = true,
)
private fun AcmeAppPreviewLight() {
    AcmeApp()
}

@Composable
@Preview(
    showBackground = true,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
private fun AcmeAppPreviewDark() {
    AcmeApp()
}

@Composable
@Preview(
    showBackground = true,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_NO,
    showSystemUi = true,
    fontScale = 2.0f,
)
private fun AcmeAppPreviewLargeFont() {
    AcmeApp()
}
