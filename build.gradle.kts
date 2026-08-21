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

@file:OptIn(ExperimentalTime::class)

import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
    alias(libs.plugins.dependency.updates) apply true
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
                    "ij_kotlin_imports_layout" to
                        "*,|,com.acme.kmp.shared.kmpshared.**,|,com.acme.kmp.compose.kmpcompose.**,|,com.acme.**",
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
        ktlint().editorConfigOverride(
            // See https://pinterest.github.io/ktlint/1.8.0/rules/standard/
            mapOf(
                "ktlint_code_style" to "ktlint_official",
                "ktlint_standard_when-entry-bracing" to "disabled",
                "ktlint_standard_no-unused-imports" to "enabled",
                "ij_kotlin_line_break_after_multiline_when_entry" to "false",
                "indent_size" to 4,
                "max_line_length" to 120,
            ),
        )
        trimTrailingWhitespace()
        endWithNewline()
        leadingTabsToSpaces(4)
    }
}

// TODO: Refactor into shared build.gradle.kts or use gradle conventions
subprojects {
    val moduleBuildConfig =
        tasks.register("moduleBuildConfig") {
            description = "Creates a `ModuleBuildConfig` for each module."

            // Use providers for lazy evaluation to capture values set in subprojects
            inputs.property("group", provider { project.group.toString() })
            inputs.property("version", provider { project.version.toString() })
            inputs.property("name", provider { project.name })
            inputs.property("description", provider { project.description ?: "" })

            val outputDir = layout.buildDirectory.dir("generated/sources/moduleBuildConfig/kotlin")
            outputs.dir(outputDir)

            doLast {
                val group = project.group.toString()
                val version = project.version.toString()
                val description = project.description ?: ""

                // Validate 'project' attributes
                if (rootProject.name == group) {
                    throw GradleException(
                        "Project 'group' must be specified in '${project.name}/build.gradle.kts' and must be different than '${rootProject.name}' (root-project name)",
                    )
                }
                if (group.isBlank()) {
                    throw GradleException(
                        "Project 'group' must be specified in '${project.name}/build.gradle.kts' and must be a valid package name",
                    )
                }
                if (version.isBlank()) {
                    throw GradleException("Project 'version' must be specified in '${project.name}/build.gradle.kts")
                }
                if (description.isBlank()) {
                    throw GradleException(
                        "Project 'description' must be specified in '${project.name}/build.gradle.kts",
                    )
                }

                val buildConfigName = "ModuleBuildConfig"
                val pkgDirs = group.replace(".", "/")
                val buildConfigFile = outputDir.get().file("$pkgDirs/$buildConfigName.kt").asFile
                val now = Clock.System.now()
                val tz = TimeZone.currentSystemDefault()
                buildConfigFile.parentFile.mkdirs()
                buildConfigFile.writeText(
                    """
                    |package $group
                    |
                    |/** Build-Config for module `${project.name}` ($description).
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
                    |    const val MODULE_NAME: String = "$description"
                    |    /** Module Version - from `build.gradle.kts` module `version`.  */
                    |    const val MODULE_VERSION: String = "$version"
                    |    /** Module Group - from `build.gradle.kts` module `group`.  */
                    |    const val MODULE_GROUP: String = "$group"
                    |}
                    """.trimMargin(),
                )
            }
        }

    // Wiring: Hook into the 'kotlin' extension regardless of the specific plugin type
    afterEvaluate {
        // Find the Kotlin extension by type to be more robust across different plugin versions/types
        val kotlinExt = extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension::class.java)

        if (kotlinExt != null) {
            try {
                val sourceSets = kotlinExt.sourceSets
                listOf("main", "commonMain").forEach { sourceSetName ->
                    sourceSets.findByName(sourceSetName)?.kotlin?.srcDir(moduleBuildConfig)
                }
            } catch (_: Exception) {
                // If direct access fails, fallback to reflection
                try {
                    val getSourceSets = kotlinExt.javaClass.getMethod("getSourceSets")
                    val sourceSets = getSourceSets.invoke(kotlinExt) as NamedDomainObjectContainer<*>
                    listOf("main", "commonMain").forEach { sourceSetName ->
                        sourceSets.findByName(sourceSetName)?.let { sourceSet ->
                            val getKotlin = sourceSet.javaClass.getMethod("getKotlin")
                            val kotlinSourceSet = getKotlin.invoke(sourceSet)
                            val srcDir = kotlinSourceSet.javaClass.getMethod("srcDir", Any::class.java)
                            srcDir.invoke(kotlinSourceSet, moduleBuildConfig)
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }

        // Ensure Android projects always run the task before building and recognize the source dir
        plugins.withId("com.android.application") {
            tasks.findByName("preBuild")?.dependsOn(moduleBuildConfig)
            extensions.findByName("android")?.let { android ->
                try {
                    val getSourceSets = android.javaClass.getMethod("getSourceSets")
                    val sourceSets = getSourceSets.invoke(android) as NamedDomainObjectContainer<*>
                    sourceSets.findByName("main")?.let { main ->
                        val getJava = main.javaClass.getMethod("getJava")
                        val javaSourceSet = getJava.invoke(main)
                        val srcDir = javaSourceSet.javaClass.getMethod("srcDir", Any::class.java)
                        srcDir.invoke(javaSourceSet, moduleBuildConfig)
                    }
                } catch (_: Exception) {
                }
            }
        }
        plugins.withId("com.android.library") {
            tasks.findByName("preBuild")?.dependsOn(moduleBuildConfig)
            extensions.findByName("android")?.let { android ->
                try {
                    val getSourceSets = android.javaClass.getMethod("getSourceSets")
                    val sourceSets = getSourceSets.invoke(android) as NamedDomainObjectContainer<*>
                    sourceSets.findByName("main")?.let { main ->
                        val getJava = main.javaClass.getMethod("getJava")
                        val javaSourceSet = getJava.invoke(main)
                        val srcDir = javaSourceSet.javaClass.getMethod("srcDir", Any::class.java)
                        srcDir.invoke(javaSourceSet, moduleBuildConfig)
                    }
                } catch (_: Exception) {
                }
            }
        }
    }
}
