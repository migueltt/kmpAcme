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

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.hot.reload) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.spotless) apply true
}

// Applies to all files in all modules.
spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        ktlint()
            .editorConfigOverride(
                // See https://pinterest.github.io/ktlint/1.8.0/rules/standard/
                mapOf(
                    "ktlint_code_style" to "ktlint_official",
                    "ktlint_standard_when-entry-bracing" to "disabled",
                    "ktlint_standard_no-unused-imports" to "enabled",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    "ij_kotlin_line_break_after_multiline_when_entry" to "false",
                    "ij_kotlin_imports_layout" to "*,|,kmpacme.**,|,com.acme.**",
                    "indent_size" to 4,
                    "max_line_length" to 120,
                ),
            )
        trimTrailingWhitespace()
        endWithNewline()
        leadingTabsToSpaces(4)
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
        leadingTabsToSpaces(4)
    }
}
