# KMP Acme
This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM), Server.

## Major changes 
* `Feb 01, 2026 - `[`tag "KMP-default"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-default) ([browse files](https://github.com/migueltt/kmpAcme/tree/c17c0e6cec5b90c4deb690f8726522b01dbb7843)):
  Initial project creation through AndroidStudio Kotlin Multiplatform Project Wizard.
* `Feb 01, 2026 - `[`tag "KMP-fixed"`](https://github.com/migueltt/kmpAcme/releases/tag/KMP-fixed) ([browse files](https://github.com/migueltt/kmpAcme/tree/c6a0078d9ca0d4d2efb8b0f12362fe213ff9e86e)):
  Initial changes right after creating a KMP Project using the Kotlin Multiplatform Project Wizard.
  - All six different applications can be executed without problems:
    - Android
    - iOS
    - Desktop (JVM)
    - Server (Ktor)
    - Web (JS)
    - Web (Wasm)
  - These changes are only to fix several issues related to gradle-plugins (kmp, cmp, android), organizing all modules and components with
  the same basic functionality. There has been several issues since Google introduced Android Gradle plugin 9+.

----
### Project Structure
* [/kmp-shared](./kmpShared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./kmpShared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.
* [/kmpCompose](./kmpCompose/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./kmpCompose/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./kmpCompose/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./kmpCompose/src/jvmMain/kotlin)
    folder is the appropriate location.
* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.
* [/app-server](./app-server/src/main/kotlin) is for the Ktor server application.
* [/app-android](./app-android/src/main/kotlin) is for the Android application.


----
### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar.

You can build and install it directly from the terminal (you must use the JDK within AndroidStudio):
- on macOS/Linux
  ```shell
  export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
  ./gradlew :app-android:installDebug
  ```
- on Windows
  ```shell
  set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat :app-android:installDebug
  ```

----
### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar.

You can run it directly from the terminal (it will use the default JDK on your system):
- on macOS/Linux
  ```shell
  export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
  ./gradlew :kmpCompose:run
  ```
- on Windows
  ```shell
  set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat :kmpCompose:run
  ```

----
### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
  ./gradlew :app-server:run
  ```
- on Windows
  ```shell
  set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat :app-server:run
  ```
Open a browser on http://localhost:8080 to see the server in action - should display something like this:
```text
Ktor: Hello, Java 21.0.8!
```

----
### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
    ./gradlew :kmpCompose:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
      set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
    .\gradlew.bat :kmpCompose:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
    ./gradlew :kmpCompose:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
      set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
    .\gradlew.bat :kmpCompose:jsBrowserDevelopmentRun
    ```

----
### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---
### Kotlin and Compose Multiplatform Resources (from KMP Project Wizard)

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).
