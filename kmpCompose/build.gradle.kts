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

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hot.reload)
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.acme.kmp.compose"
description = "Acme KMP Shared"
version = "0.1.0"

kotlin {
    compilerOptions {
        // Enable all warnings as errors
        // allWarningsAsErrors.set(true)

        // Enable experimental features
        freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
        // freeCompilerArgs.add("-Xexplicit-backing-fields")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
    androidLibrary {
        namespace = "com.acme.kmp.compose"
        compileSdk =
            libs.versions.androidSdkCompile
                .get()
                .toInt()
        minSdk =
            libs.versions.androidSdkMin
                .get()
                .toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
        androidResources {
            enable = true
        }
    }
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KmpCompose"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
        }
        commonMain.dependencies {
            implementation(projects.kmpShared)
            implementation(libs.bundles.compose.multiplatform)
            implementation(libs.bundles.compose.lifecycle)
            implementation(libs.bundles.compose.navigation)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

dependencies {
    // This is required for AndroidStudio built-in Preview for components under `commonMain`.
    "androidRuntimeClasspath"(libs.compose.ui.tooling)
}

compose {
    desktop {
        application {
            mainClass = "com.acme.kmp.compose.MainKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "com.acme.app"
                packageVersion = "1.0.0"
            }
        }
    }
}
