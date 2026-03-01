@file:OptIn(ExperimentalTime::class)

import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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

buildscript {
    dependencies {
        classpath(libs.kotlinx.datetime)
    }
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

// TODO: Refactor into shared build.gradle.kts or use gradle conventions
subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        // These tasks only apply to those using KMP plugin.
        configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
            // This task creates a `ModuleBuildConfig` for each module using KMP plugin.
            // The resulting class is under `commonMain` sourceSet.
            val kmpModuleBuildConfig by tasks.registering {
                if (project.group.toString().isBlank()) {
                    throw GradleException("Project 'group' must be specified in '${project.name}/build.gradle.kts'")
                }
                if (project.version.toString().isBlank()) {
                    throw GradleException("Project 'version' must be specified in '${project.name}/build.gradle.kts")
                }
                if (project.description.isNullOrBlank()) {
                    throw GradleException("Project 'description' must be specified in '${project.name}/build.gradle.kts")
                }
                // Track changes on module `build.gradle.kts`
                inputs.file("build.gradle.kts")
                val buildConfigName = "ModuleBuildConfig"
                val buildDirCommonMain =
                    layout.buildDirectory.dir("generated/sources/buildConfig/src/commonMain/kotlin").also {
                        // Register output - to be added into `sourceSets.commonMain`
                        outputs.dir(it)
                    }
                val pkgDirs = project.group.toString().replace(".", "/")
                val buildConfigFile = buildDirCommonMain.get().file("$pkgDirs/$buildConfigName.kt").asFile
                doLast {
                    val now = Clock.System.now()
                    val tz = TimeZone.currentSystemDefault()
                    buildConfigFile.parentFile.mkdirs()
                    buildConfigFile.writeText(
                        """
                        |package ${project.group}
                        |
                        |/** Module Build-Config for `${project.name}`.
                        | *
                        | * Generated on:
                        | * ```
                        | * ${now.toLocalDateTime(tz)}${tz.offsetAt(now)}
                        | * ```
                        | */
                        |object $buildConfigName {
                        |    /** Module Identifier - from `build.gradle.kts` module `name`. */
                        |    const val MODULE_ID: String = "${project.name}"
                        |    /** Module Name - from `build.gradle.kts` module `description`. */
                        |    const val MODULE_NAME: String = "${project.description}"
                        |    /** Module Version - from `build.gradle.kts` module `version`.  */
                        |    const val MODULE_VERSION: String = "${project.version}"
                        |    /** Module Group - from `build.gradle.kts` module `group`.  */
                        |    const val MODULE_GROUP: String = "${project.group}"
                        |}
                        """.trimMargin(),
                    )
                }
            }
            sourceSets.commonMain {
                kotlin.srcDir(kmpModuleBuildConfig)
            }
        }
    }
}
