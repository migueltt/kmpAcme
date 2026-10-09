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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource

import com.acme.kmp.compose.kmpcompose.generated.resources.Res
import com.acme.kmp.compose.kmpcompose.generated.resources.compose_multiplatform

import com.acme.kmp.compose.theme.AppTheme
import com.acme.kmp.compose.theme.Theme
import com.acme.kmp.shared.Greeting

/** Main composable application with adaptive layout support.
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
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWideScreen = maxWidth >= 800.dp
                val scrollState = rememberScrollState()

                if (isWideScreen) {
                    // Wide / Multi-pane layout for tablets, desktop, foldables
                    Row(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .verticalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        ElevatedCard(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                        ) {
                            GreetingSection(
                                showGreeting = showGreeting,
                                onToggleGreeting = { showGreeting = !showGreeting },
                                greeting = greeting,
                            )
                        }

                        ElevatedCard(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                        ) {
                            ApiResultsSection(
                                showApiResults = showApiResults,
                                onToggleApiResults = { showApiResults = !showApiResults },
                                viewModel = viewModel,
                            )
                        }
                    }
                } else {
                    // Single column layout for compact screens (e.g. portrait phones)
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            GreetingSection(
                                showGreeting = showGreeting,
                                onToggleGreeting = { showGreeting = !showGreeting },
                                greeting = greeting,
                            )
                        }

                        // HorizontalDivider(Modifier.padding(vertical = 16.dp))

                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            ApiResultsSection(
                                showApiResults = showApiResults,
                                onToggleApiResults = { showApiResults = !showApiResults },
                                viewModel = viewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GreetingSection(
    showGreeting: Boolean,
    onToggleGreeting: () -> Unit,
    greeting: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onToggleGreeting) {
            Text(if (showGreeting) "Hide Greeting" else "Click me!")
        }
        AnimatedVisibility(showGreeting) {
            Column(
                modifier = Modifier.fillMaxWidth(fraction = 0.6f).padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Spacer(Modifier.height(8.dp))
                Text("Compose: $greeting")
            }
        }
    }
}

@Composable
private fun ApiResultsSection(
    showApiResults: Boolean,
    onToggleApiResults: () -> Unit,
    viewModel: AcmeViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onToggleApiResults) {
            Text(if (showApiResults) "Hide API Options" else "Call API")
        }
        AnimatedVisibility(showApiResults) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ApiResults(viewModel)
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

@Composable
@Preview(
    name = "Phone",
    device = Devices.PHONE,
    showBackground = true,
)
private fun AcmeAppPreviewPhone() {
    AcmeApp()
}

@Composable
@Preview(
    name = "Foldable",
    device = Devices.FOLDABLE,
    showBackground = true,
)
private fun AcmeAppPreviewFoldable() {
    AcmeApp()
}

@Composable
@Preview(
    name = "Tablet",
    device = Devices.TABLET,
    showBackground = true,
)
private fun AcmeAppPreviewTablet() {
    AcmeApp()
}

@Composable
@Preview(
    name = "Desktop",
    device = Devices.DESKTOP,
    showBackground = true,
)
private fun AcmeAppPreviewDesktop() {
    AcmeApp()
}
