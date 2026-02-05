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

package com.acme.mobile.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview

import com.acme.kmp.compose.App
import com.acme.kmp.compose.theme.Theme

/** Android entry point of the application.
 *
 * @see App
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // If you only need to support Dark/Light themes, do not include `getColorScheme()`.
            App()
            // App(colorScheme = getColorScheme())
        }
    }
}

/** Helper function to detect dynamic color schemes.
 *
 * **WARNING:** Supporting Dynamic Colors invalidates your "organization" theme.
 */
@Composable
private fun getColorScheme(): ColorScheme {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    // Implemented here since `kmpCompose` does not have access to `android.os.Build`
    // This logic could potentially be moved to `kmpCompose` itself using expect/actual if
    // other platforms have different version requirements.
    val isApi31 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    return when {
        isApi31 && isDarkTheme -> dynamicDarkColorScheme(context)
        isApi31 -> dynamicLightColorScheme(context)
        isDarkTheme -> Theme.darkScheme
        else -> Theme.lightScheme
    }
}

@Composable
@Preview(
    showBackground = true,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
private fun AppPreviewDark() {
    App()
}

@Composable
@Preview(
    showBackground = true,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_NO,
    showSystemUi = true,
)
private fun AppPreviewLight() {
    App()
}

@Composable
@Preview(
    showBackground = true,
    uiMode = AndroidUiModes.UI_MODE_NIGHT_NO,
    showSystemUi = true,
    fontScale = 2.0f,
)
private fun AppPreviewLargeFont() {
    App()
}
