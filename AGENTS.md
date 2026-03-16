# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

ChatSpring is a native Android (Kotlin) app — a GPT-based AI toolkit. Single-module Gradle project, no Docker or backend services needed.

### Environment

- **JDK 17** is required (Gradle 8.0 / AGP 8.0.2 do not support JDK 21+).
- **Android SDK** at `/opt/android-sdk` with platform API 33 and build-tools 33.0.2.
- Environment variables `JAVA_HOME`, `ANDROID_HOME`, `ANDROID_SDK_ROOT` are persisted in `~/.bashrc`.
- `local.properties` must point `sdk.dir` to `/opt/android-sdk` (the update script handles this automatically).

### Build, test, and lint

All commands run from the repo root (`/workspace`):

| Task | Command |
|------|---------|
| Debug build | `./gradlew assembleDebug` |
| Unit tests | `./gradlew testDebugUnitTest` |
| Lint | `./gradlew lint` |

### Known issues

- `./gradlew lint` will fail with a pre-existing `NotificationPermission` error from the Bmob SDK dependency — this is a codebase issue, not an environment problem.
- There are no instrumented (Espresso) tests runnable without an Android emulator/device, which is not available in this cloud environment.
- The app requires an OpenAI API key at runtime and a Bmob backend for user auth; these are cloud services and not needed for building/testing.
