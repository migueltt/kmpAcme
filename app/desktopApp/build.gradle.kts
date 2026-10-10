import org.jetbrains.compose.desktop.application.dsl.TargetFormat

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
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

group = "com.acme.desktop"
description = "Acme Desktop App"
version = "0.1.0"

dependencies {
    implementation(project(":kmpCompose"))
    implementation(libs.compose.ui.tooling.preview)
}

compose.desktop {
    application {
        mainClass = "com.acme.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.acme.desktop"
            packageVersion = "1.0.0"
        }
    }
}
