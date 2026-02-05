# Theme, Colors
Setting a theme / colors for Compose Multiplatform is quite simple.

## 1. Select your colors
Go to https://material-foundation.github.io/material-theme-builder and select your colors.

For this reference application, the source primary color is
[#415f91](https://material-foundation.github.io/material-theme-builder/?primary=%23415f91).

## 2. Select Font
This step is optional. Choose a font and the resulting code will include mechanisms.

Check [README_Typography.md](./README_Typography.md) if you want to use https://fonts.google.com

## 3. Expand and copy to your project
Locate and expand the zip file and copy the files to your project

## 4. Adjustments
Based on the multiple files, some adjustments and refactoring was applied:
- See [Palette.kt](src/commonMain/kotlin/com/acme/kmp/compose/theme/Palette.kt)
- See [Theme.kt](src/commonMain/kotlin/com/acme/kmp/compose/theme/Theme.kt)

## 5. Set your theme 
- How to define your - see [Theme.kt](src/commonMain/kotlin/com/acme/kmp/compose/theme/Theme.kt)
- How to use your theme - see [App.kt](src/commonMain/kotlin/com/acme/kmp/compose/App.kt)
