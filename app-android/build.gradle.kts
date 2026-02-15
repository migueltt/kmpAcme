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
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    compilerOptions {
        // Enable all warnings as errors
        // allWarningsAsErrors.set(true)

        // Enable experimental features
        freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
    dependencies {
        implementation(projects.kmpCompose)
        implementation(libs.androidx.compose.activity)
        implementation(libs.compose.foundation)
        implementation(libs.compose.material3)
        testImplementation(libs.kotlin.test.junit)
        androidTestImplementation(libs.bundles.android.test)
        // Not really required - all @Preview's should be in kmpCompose
        // Enable to use `@Preview` and `AndroidUiModes`
        implementation(libs.compose.ui.tooling.preview)
        // Not really required - all @Preview's should be in kmpCompose
        // Enable to visualize @Preview in AndroidStudio
        debugImplementation(libs.compose.ui.tooling)
    }
}

android {
    namespace = "com.acme.mobile.app"
    defaultConfig {
        applicationId = "com.acme.mobile.app"
        compileSdk =
            libs.versions.androidSdkCompile
                .get()
                .toInt()
        minSdk =
            libs.versions.androidSdkMin
                .get()
                .toInt()
        targetSdk =
            libs.versions.androidSdkTarget
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
