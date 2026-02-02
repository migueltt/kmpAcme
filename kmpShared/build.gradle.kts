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

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    compilerOptions {
        // Enable all warnings as errors
        // allWarningsAsErrors.set(true)

        // Enable experimental features
        freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
    androidLibrary {
        namespace = "com.acme.kmp.shared"
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
    iosArm64()
    iosSimulatorArm64()

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
        commonMain.dependencies {
            api(libs.bundles.kotlinx.core)
            implementation(libs.bundles.ktor.client)
            // TODO: Bug in IntelliJ/AndroidStudio?
            // Module `:app-server` compiles/runs OK, but IDE shows:
            // Cannot access 'Pipeline' which is a supertype of 'Application'. Check your module classpath for missing or conflicting dependencies
            api(libs.ktor.utils)
            // Module `:app-server` compiles/runs OK, but IDE cannot resolve references to `Json`:
            api(libs.kotlinx.serialization.json)
            // Module `:app-server` compiles/runs OK, but IDE cannot resolve references to `Json`:
            api(libs.ktor.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
    }
}
