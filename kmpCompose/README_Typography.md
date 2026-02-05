# Font and Typography
Setting a font for Compose Multiplatform is quite simple.

## 1. Select your font
Go to https://fonts.google.com and select your font.

For this reference application,
[Atma](https://fonts.google.com/specimen/Atma?preview.text=ABCD%20abcd%201234%20!@%23$()%0A&categoryFilters=Feeling:%2FExpressive%2FPlayful&query=atma&specimen.preview.text=ABCD+abcd+1234+!@%23$()%0A&preview.script=Latn&preview.lang=en_Latn)
was selected.

## 2. Download font
Click on `Get font` and select `Download all`.

## 3. Expand and copy to your project
Locate and expand the zip file.
There will be several `.ttf` files, depending on the the different styles it supports.
There's also a license file that you should also include into your project.
Copy all the files into `src/commonMain/composeResources/font`.
Optionally, rename the license file with the same prefix as your selected font.

**IMPORTANT:** Depending on the font you choose, it may come with static and/or variable font files.
As always, it is a matter of balancing:
- Overall app size -> more files, larger the APK.
- Memory usage -> static fonts only allocates for the specific weight. Variable fonts are slightly larger.
- Runtime -> Variable fonts are slightly slower, since CPU must calculate glyph sizes.
- Compatibility -> Static fonts work on all Android versions

## 4. Setting typography to your theme
See [Typography.kt](src/commonMain/kotlin/com/acme/kmp/compose/theme/Typography.kt)
