# Contributing to AndrOBD

Thanks for your interest in contributing! This guide covers everything you need to get started.

---

## Table of contents

1. [Getting the code](#getting-the-code)
2. [Building the app](#building-the-app)
3. [Testing without hardware — Demo mode](#testing-without-hardware--demo-mode)
4. [Coding conventions](#coding-conventions)
5. [Submitting changes](#submitting-changes)
6. [Translations](#translations)
7. [Plugin development](#plugin-development)
8. [AI-assisted contributions](#ai-assisted-contributions)
9. [Community](#community)

---

## Getting the code

AndrOBD uses a Git submodule for the plugin framework. You **must** clone with `--recurse-submodules` or the build will fail:

```bash
git clone https://github.com/fr3ts0n/AndrOBD.git --recurse-submodules
```

If you already have a plain clone:

```bash
git submodule update --init --recursive
```

---

## Building the app

Open the project in [Android Studio](https://developer.android.com/studio) and let Gradle sync.
From the command line:

```bash
./gradlew build          # build all variants + run tests
./gradlew assembleDebug  # debug APK only
```

**Minimum requirements:** JDK 17, Android SDK with build-tools for API 36.

---

## Testing without hardware — Demo mode

You can exercise most app features without an OBD adapter or a vehicle:

1. Turn **off** Bluetooth on your device (or use an emulator with no BT).
2. In AndrOBD settings, set **OBD Settings → Adapter type** to **Bluetooth**.
3. Launch the app and **reject** the Bluetooth permission request.

AndrOBD will enter **Demo mode**, streaming simulated vehicle data.

---

## Coding conventions

- **Language:** Java is the primary language. Kotlin is also supported (since v2.0) and is welcome for new files.
- **Style:** Follow the conventions of the surrounding code. Indentation is tabs.
- **Logging:** Use the existing `java.util.logging.Logger` instance in each class — avoid `System.out`, `System.err`, or `e.printStackTrace()`.
- **Backwards compatibility:** The app targets a wide API range (17–36). Be careful to guard any API that was introduced after API 17 with a `Build.VERSION.SDK_INT` check, or note in your PR if you are intentionally raising the minimum.
- **Tests:** New logic is welcome with JUnit tests under `androbd/src/test/`. The existing test suite can be run with `./gradlew test`.

---

## Submitting changes

1. **Open or comment on an issue first.** This avoids duplicated effort and lets the maintainer give early feedback on the approach.
2. **Check for open PRs** on the same topic before starting — search by title as well as issue cross-references.
3. **Fork the repository** and create a branch from `master`.
4. **Keep PRs small and focused** — one concern per PR makes review much faster.
5. **Describe what and why** in the PR body. Include steps to test, and note whether you were able to test with a real OBD adapter.

---

## Translations

Strings are managed through [Weblate](https://hosted.weblate.org/projects/androbd/). Please make translation changes there rather than editing `res/values-*/strings.xml` directly.

- [App strings](https://hosted.weblate.org/projects/androbd/strings/)
- [OBD data descriptions](https://hosted.weblate.org/projects/androbd/obd-data-descriptions/)
- [Fault code descriptions](https://hosted.weblate.org/projects/androbd/fault-codes/)

---

## Plugin development

AndrOBD has a plugin system that lets you add new data sources without modifying the core app. See the [AndrOBD-Plugin repository](https://github.com/fr3ts0n/AndrOBD-Plugin) for the framework and example plugins.

---

## AI-assisted contributions

AI tools (including GitHub Copilot and other assistants) are welcome for drafting code or documentation. If your contribution was substantially generated or guided by an AI tool, please say so briefly in the PR description — for example: *"Drafted with GitHub Copilot, reviewed and tested manually."* This helps the maintainer understand the provenance of the code and focus review effort appropriately.

---

## Community

- **Telegram:** [AndrOBD group](https://t.me/joinchat/G60ltQv5CCEQ94BZ5yWQbg)
- **Matrix:** [#AndrOBD:matrix.org](https://matrix.to/#/#AndrOBD:matrix.org)
- **Wiki:** [GitHub Wiki](https://github.com/fr3ts0n/AndrOBD/wiki) — includes the FAQ, Demo mode instructions, and build troubleshooting
