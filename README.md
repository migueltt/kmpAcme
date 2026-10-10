# KMP Acme
This is a Kotlin Multiplatform PoC/reference project targeting Android, iOS, Web, Desktop (JVM), Server.

Based on the concepts around [Dynamic Content](https://medium.com/dynamic-content) and its technical implementation
using Kotlin Multiplatform and Compose Multiplatform:
1. [Dynamic Mobile Apps](https://medium.com/dynamic-content/dynamic-mobile-apps-b727f946aa14)
2. [Dynamic Content Strategy](https://medium.com/dynamic-content/dynamic-content-strategy-d824570b7302)
3. [Dynamic Content Architecture](https://medium.com/dynamic-content/dynamic-content-strategy-d824570b7302)
4. _More to come in the future..._

Related technical articles:
1. [Kotlin Multiplatform Setup](https://medium.com/dynamic-content/kotlin-multiplatform-setup-84cdcc4d6341)
   _(which references this repository)_
2. _More to come in the future..._

----
### AndroidStudio
- `Android Studio Rabbit 1 | 2026.2.1 Build #AI-262.9437.185.2621.16467767` (built on September 29, 2026)
- Kotlin Multiplatform plugin `262.9437.133-AS`
- Previews work on `kmpCompose` and `androidApp` modules without issues.

----
## Recent changes
Go back in time through tags for each major change.

* `October 09, 2026 - `[`tag "KMP-2026-10-09-structure"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-2026-10-09-structure) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-2026-10-09-structure))
  - Major refactoring to match the official Kotlin Multiplatform project structure.
  - The structure is similar to the one created using KMP plugin, except for:
    - Module `:kmpServerApp` is within `/kmpServerApp` directory and includes the mock Ktor server.
    - Module `:kmpShared` is within `/kmpShared` directory and includes shared data-models
      and utilities to make API calls.
    - Module `:kmpCompose` is within `/kmpCompose` directory and includes the UI components,
      using `:kmpShared` to make API calls.
    - Any module under `"app"` follows the same naming convention.
* `October 08, 2026 - `[`tag "KMP-2026-10-08-api-updates"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-2026-10-08-api-updates) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-2026-10-08-api-updates))
  - Minor libs and Gradle updates
  - Refactoring and improvements to API calls
  - Cleaning up some functions
* `August 20, 2026 - `[`tag "KMP-2026-08-20-api-call"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-2026-08-20-api-call) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-2026-08-20-api-call))
  - Minor libs and Gradle updates
  - Includes changes to call API endpoint included in `kmpServerApp`
  - Composable updates to include delay and mode to simulate different states
  - Introduces simple patterns for ViewModels, API calls, and state evaluation
  - Includes polymorphic adapters to handle different data types for dynamic JSON payloads

See [older changes](#Older-changes)

----
### Project Structure
* [`/kmpShared`](./kmpShared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./kmpShared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.
* [`/kmpCompose`](./kmpCompose/src) is for code that will be shared across your Compose Multiplatform 
  applications and uses 'kmpShared' to make API calls. All your UI logic must be written in this
  module.
  
  It contains several subfolders:
  - [commonMain](./kmpCompose/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./kmpCompose/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./kmpCompose/src/jvmMain/kotlin)
    folder is the appropriate location.
* [`/kmpServerApp`](kmpServerApp) is for the Ktor server application and implements 'kmpShared'.
* Client apps: all of them implement module 'kmpCompose' - all these modules should have minimal
  code, except when required (like using platform-specific APIs, like sensors where applicable).
  - [`/app/androidApp`](app/androidApp) is for the Android application.
  - [`/app/desktopApp`](app/desktopApp) is for the Desktop application.
  - [`/app/iosApp`](app/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
    you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.
  - [`/app/webApp`](app/webApp) is for the Web application - both `js` (javascript) and `wasm` (webAssembly).


----
### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar.

You can build and install it directly from the terminal (you must use the JDK within AndroidStudio):
- on macOS/Linux
  ```shell
  export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
  ./gradlew :app:androidApp:installDebug
  ```
- on Windows
  ```shell
  set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat :app:androidApp:installDebug
  ```

----
### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar.

You can run it directly from the terminal (it will use the default JDK on your system):
- on macOS/Linux
  ```shell
  export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
  ./gradlew :app:desktopApp:run
  ```
    ```shell
  export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
  ./gradlew :app:desktopApp:hotRun --auto
  ```
- on Windows
  ```shell
  set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat :app:desktopApp:run
  ```
  ```shell
  set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat :app:desktopApp:hotRun --auto
  ```

----
### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
  ./gradlew :kmpServerApp:run
  ```
- on Windows
  ```shell
  set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat :kmpServerApp:run
  ```
Open a browser on http://localhost:8080/acme/data?mode=Success to see the server in action - should display something like this:
```JSON
{
    "clazz": "AcmeData",
    "platform": "Java 25.0.3",
    "timestamp": "2026-10-09T21:53:19.385465",
    "module": {
        "clazz": "ModuleInfo",
        "id": "kmpServerApp",
        "name": "Acme Server App",
        "group": "com.acme.server.app",
        "version": "0.1.0"
    }
}
```

----
### Build and Run Web Application
- Run `./gradlew kotlinWasmUpgradeYarnLock` if errors with Yarn when running `app.webApp [wasmJs]`
- Run `./gradlew kotlinUpgradeYarnLock` if errors with Yarn when running `app:webApp [js]`

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
    ./gradlew :app:webApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
    .\gradlew.bat :app:webApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
    ./gradlew :app:webApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
    .\gradlew.bat :app:webApp:jsBrowserDevelopmentRun
    ```

----
### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/app/iosApp](app/iosApp) directory in Xcode and run it from there.

---
### Kotlin and Compose Multiplatform Resources (from KMP Project Wizard)

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).

---
## Older changes
Go back in time through tags for each major change.

* `August 06, 2026 - `[`tag "KMP-2026-08-06-gradle"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-2026-08-06-gradle) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-2026-08-06-gradle))
  - Gradle update to `9.7.0`
  - Other libraries upgrade
* `July 27, 2026 - `[`tag "KMP-2026-07-27-gradle"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-2026-07-27-gradle) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-2026-07-27-gradle))
  - Gradle update to `9.6.1`
  - Android Gradle plugin to `9.3.1`
  - Other libraries upgrade
* `July 16, 2026 - `[`tag "KMP-2026-07-16-gradle"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-2026-07-06-gradle) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-2026-07-16-gradle))
  - Gradle update to `9.6.1`
  - Android Gradle plugin to `9.3.0`
  - Other libraries upgrade
* `July 06, 2026 - `[`tag "KMP-2026-07-06-gradle"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-2026-07-06-gradle) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-2026-07-06-gradle))
  - Libraries update for ktor, compose, collections, spotless.
* `June 08, 2026 - `[`tag "KMP-2026-06-08-gradle"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-2026-06-08-gradle) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-2026-06-08-gradle))
  - Mainly, Kotlin update to `2.4.0`
* `May 23, 2026 - `[`tag "KMP-23-05-2026-gradle"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-23-05-2026-gradle) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-23-05-2026-gradle))
  - Gradle upgraded to `v9.5.1` and related Android plugin to `v9.2.1`.
  - Libraries and plugins upgraded to their most recent stable versions, as applicable.
  - Minor formatting updates
  - Module `kmpServerApp` default implementation includes CORS headers.
  - All targets (Android, iOS, web-js, web-wasmJs, desktop) work without issues.
    - iOS app tested with iOS `v26.5`.
    - Android app targets API 37.
* `May 12, 2026 - `[`tag "KMP-04-2026-gradle"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-04-2026-gradle) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-04-2026-gradle))
  - Upgrade gradle and libraries
  - Minor formatting updates
* `Mar 22, 2026 - `[`tag "KMP-03-2026-gradle"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-03-2026-gradle) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-03-2026-gradle))
  - Upgrade gradle and libraries
  - Include `BuildConfig` for Android.
  - Include `ModuleBuildConfig` for Kotlin Multiplatform modules.
* `Mar 01, 2026 - `[`tag "KMP-themed-2"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-themed-2) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-themed-2))
  - Some fixes and updates on documentation.
  - Add gradle task to generate `BuildConfig` for kmp-modules.
* `Feb 04, 2026 - `[`tag "KMP-themed"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-themed) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-themed))
  - Applying theme and fonts.
  - Support for dark/light themes, using [Material Theme Builder](https://material-foundation.github.io/material-theme-builder) - see [README_Theme.md](./kmpCompose/README_Theme.md)
  - Custom font and how to set it up through [Google Fonts](http://fonts.google.com) - see [README_Typography.md](./kmpCompose/README_Typography.md)
  - Overall theme and typography structure for easier maintenance
* `Feb 01, 2026 - `[`tag "KMP-fixed"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-fixed) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-fixed)):
  Initial changes right after creating a KMP Project using the Kotlin Multiplatform Project Wizard.
  - All six different applications can be executed without problems:
    - Android
    - iOS
    - Desktop (JVM), Hot reload
    - Server (Ktor)
    - Web (JS)
    - Web (Wasm)
  - These changes are only to fix several issues related to gradle-plugins (kmp, cmp, android), organizing all modules and components with
    the same basic functionality. There has been several issues since Google introduced Android Gradle plugin 9+.
* `Feb 01, 2026 - `[`tag "KMP-default"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-default) ([browse files](https://github.com/migueltt/kmpAcme/tree/KMP-default)):
  Initial project creation through AndroidStudio Kotlin Multiplatform Project Wizard.

