package com.acme.kmp.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font

import kmpacme.kmpcompose.generated.resources.Atma_Bold
import kmpacme.kmpcompose.generated.resources.Atma_Light
import kmpacme.kmpcompose.generated.resources.Atma_Medium
import kmpacme.kmpcompose.generated.resources.Atma_Regular
import kmpacme.kmpcompose.generated.resources.Atma_SemiBold
import kmpacme.kmpcompose.generated.resources.Res

/** All typography is set once within [AppTheme].
 *
 * See `/kmpCompose/README_Typography.md` for more details.
 *
 * @see AppTheme
 */
internal val AppTypography: Typography
    @Composable
    get() {
        // Default Material 3 typography values
        val baseline = Typography()
        return baseline.overrideFontFamily()
    }

/** Overrides any defaults on the baseline [Typography].
 *
 * This simple implementation uses static font.
 *
 * Check [variable fonts](https://developer.android.com/develop/ui/compose/text/fonts#variable-fonts) for more information.
 */
@Composable
private fun Typography.overrideFontFamily(): Typography {
    // If different fonts are used, define them here...
    val standard =
        FontFamily(
            Font(Res.font.Atma_Light, weight = FontWeight.Light),
            Font(Res.font.Atma_Regular, weight = FontWeight.Normal),
            Font(Res.font.Atma_Medium, weight = FontWeight.Medium),
            Font(Res.font.Atma_SemiBold, weight = FontWeight.SemiBold),
            Font(Res.font.Atma_Bold, weight = FontWeight.Bold),
        )
    // ... and assign them to the Typography's.
    // In this specific case, we are using the same font across all text styles.
    return this.copy(
        displayLarge = displayLarge.copy(fontFamily = standard),
        displayMedium = displayMedium.copy(fontFamily = standard),
        displaySmall = displaySmall.copy(fontFamily = standard),
        headlineLarge = headlineLarge.copy(fontFamily = standard),
        headlineMedium = headlineMedium.copy(fontFamily = standard),
        headlineSmall = headlineSmall.copy(fontFamily = standard),
        titleLarge = titleLarge.copy(fontFamily = standard),
        titleMedium = titleMedium.copy(fontFamily = standard),
        titleSmall = titleSmall.copy(fontFamily = standard),
        bodyLarge = bodyLarge.copy(fontFamily = standard),
        bodyMedium = bodyMedium.copy(fontFamily = standard),
        bodySmall = bodySmall.copy(fontFamily = standard),
        labelLarge = labelLarge.copy(fontFamily = standard),
        labelMedium = labelMedium.copy(fontFamily = standard),
        labelSmall = labelSmall.copy(fontFamily = standard),
    )
}
