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

/** KMP compose platform abstraction.
 * Similar to [com.acme.kmp.shared.Platform] but may include UI specific attributes.
 */
abstract class UiPlatform {
    /** Module ID based on `kmpCompose` gradle `name` configuration. */
    val moduleId: String = ModuleBuildConfig.MODULE_ID

    /** Module name based on `kmpCompose` gradle `description` configuration. */
    val moduleName: String = ModuleBuildConfig.MODULE_NAME

    /** Module Version based on `kmpCompose` gradle `version` configuration. */
    val moduleVersion: String = ModuleBuildConfig.MODULE_VERSION

    /** Module base package based on `kmpCompose` gradle `group` configuration. */
    val modulePackage: String = ModuleBuildConfig.MODULE_GROUP

    /** Platform name. */
    abstract val name: String
}

/** Should provide the specific platform implementation. */
expect fun getUiPlatform(): UiPlatform
