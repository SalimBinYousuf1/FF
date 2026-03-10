# 🧮 Calculator — Flutter + C++ (Android)

A fully-featured calculator app where the **UI is Flutter/Dart** and the **math engine is native C++**, connected via **Dart FFI**.

---

## Architecture

```
┌─────────────────────────────────────────────┐
│                Flutter (Dart)                │
│  CalculatorScreen → CalculatorController     │
│                        │                    │
│              CalculatorFFI (dart:ffi)        │
└──────────────────────┬──────────────────────┘
                       │ FFI call
┌──────────────────────▼──────────────────────┐
│         libcalculator_native.so (C++)        │
│  calculator.cpp  ←  calculator_jni.cpp      │
│  • add / subtract / multiply / divide       │
│  • sqrt / power / percentage / modulo       │
│  • recursive-descent expression evaluator  │
└─────────────────────────────────────────────┘
```

---

## Project Structure

```
calculator_app/
├── pubspec.yaml                    # Flutter dependencies
├── lib/
│   ├── main.dart                   # App entry point
│   ├── calculator_controller.dart  # Business logic + state
│   ├── native/
│   │   └── calculator_ffi.dart     # Dart FFI bindings → C++
│   └── ui/
│       ├── calculator_screen.dart  # Main screen
│       └── calc_button.dart        # Reusable button widget
├── android/
│   ├── build.gradle
│   ├── settings.gradle
│   └── app/
│       ├── build.gradle            # NDK + CMake config
│       └── src/main/
│           ├── AndroidManifest.xml
│           ├── cpp/
│           │   ├── calculator.h          # C++ engine header
│           │   ├── calculator.cpp        # C++ engine impl
│           │   ├── calculator_jni.cpp    # FFI + JNI exports
│           │   └── CMakeLists.txt        # CMake build
│           └── java/com/example/calculator/
│               └── CalcBridge.java      # JNI bridge (optional)
└── test/
    ├── cpp/
    │   └── calculator_test.cpp    # 44 C++ unit tests
    └── dart/
        ├── calculator_controller_test.dart
        └── calculator_widget_test.dart
```

---

## Prerequisites

| Tool | Version |
|------|---------|
| Flutter | 3.x |
| Dart | 3.x |
| Android Studio | Hedgehog+ |
| NDK | 25.2.9519653 |
| CMake | 3.18.1+ |
| g++ (for C++ tests) | 11+ |

---

## Creating the App from Command Line (from scratch)

### Step 1 — Install Flutter
```bash
git clone https://github.com/flutter/flutter.git ~/flutter
export PATH="$HOME/flutter/bin:$PATH"

# Verify installation (fix any issues it reports)
flutter doctor

# Accept Android licenses if prompted
flutter doctor --android-licenses
# Press y to accept all
```

### Step 2 — Install Android NDK & CMake
```bash
sdkmanager "ndk;25.2.9519653"
sdkmanager "cmake;3.18.1"
```

### Step 3 — Create the Flutter Project
```bash
flutter create calculator_app
cd calculator_app

# Add FFI dependency
flutter pub add ffi
flutter pub get
```

### Step 4 — Create Directory Structure
```bash
mkdir -p android/app/src/main/cpp
mkdir -p android/app/src/main/java/com/example/calculator
mkdir -p lib/native lib/ui
mkdir -p test/cpp test/dart
```

### Step 5 — Add C++ Source Files
Copy the following files from this project into `android/app/src/main/cpp/`:
- `calculator.h`
- `calculator.cpp`
- `calculator_jni.cpp`
- `CMakeLists.txt`

### Step 6 — Configure Android NDK Build

Edit `android/app/build.gradle` and add inside `defaultConfig {}`:
```groovy
externalNativeBuild {
    cmake {
        cppFlags "-std=c++17"
        arguments "-DANDROID_STL=c++_shared"
    }
}
ndk {
    abiFilters "arm64-v8a", "armeabi-v7a", "x86_64"
}
```

Also add at the `android {}` level (outside `defaultConfig`):
```groovy
ndkVersion "25.2.9519653"
externalNativeBuild {
    cmake {
        path "src/main/cpp/CMakeLists.txt"
        version "3.18.1"
    }
}
```

### Step 7 — Add Dart/Flutter Source Files
Copy the following into `lib/`:
```bash
# Replace default main.dart and add:
lib/main.dart
lib/calculator_controller.dart
lib/native/calculator_ffi.dart
lib/ui/calculator_screen.dart
lib/ui/calc_button.dart
```

Also copy the JNI Java bridge:
```bash
android/app/src/main/java/com/example/calculator/CalcBridge.java
```

### Step 8 — Build & Run

```bash
# Check connected devices
flutter devices

# Run on connected Android device/emulator
flutter run

# Build release APK
flutter build apk --release

# APK output location:
ls build/app/outputs/flutter-apk/app-release.apk
```

### Step 9 — Install APK via ADB
```bash
adb install build/app/outputs/flutter-apk/app-release.apk

# Verify install
adb shell pm list packages | grep calculator
```

### Quick Reference — Full Flow
```bash
git clone https://github.com/flutter/flutter.git ~/flutter
export PATH="$HOME/flutter/bin:$PATH"
flutter create calculator_app && cd calculator_app
flutter pub add ffi && flutter pub get
mkdir -p android/app/src/main/cpp lib/native lib/ui test/cpp test/dart
# ... copy source files ...
flutter run
flutter build apk --release
adb install build/app/outputs/flutter-apk/app-release.apk
```

---

## Build & Run (existing project)

### 1. Clone / open project
```bash
cd calculator_app
flutter pub get
```

### 2. Run on Android device/emulator
```bash
flutter run
```
Flutter will automatically trigger the CMake/NDK build for the C++ library.

### 3. Build release APK
```bash
flutter build apk --release
# Output: build/app/outputs/flutter-apk/app-release.apk
```

---

## Running Tests

### C++ Unit Tests (host machine)
```bash
cd calculator_app
g++ -std=c++17 \
  test/cpp/calculator_test.cpp \
  android/app/src/main/cpp/calculator.cpp \
  -I android/app/src/main/cpp \
  -o /tmp/calc_test && /tmp/calc_test
```
Expected: **44 passed, 0 failed**

### Flutter/Dart Tests (on device or emulator for FFI)
```bash
# Widget tests (host)
flutter test test/dart/calculator_widget_test.dart

# Integration tests (requires connected Android device)
flutter test test/dart/calculator_controller_test.dart
```

---

## Features

| Feature | Details |
|---------|---------|
| Basic ops | +, −, ×, ÷ |
| Square root | √ via C++ std::sqrt |
| Percentage | % conversion |
| Power | xⁿ |
| Sign toggle | +/− |
| Expression eval | Full recursive-descent parser in C++ |
| Error handling | Division by zero, √(negative), bad expr |
| Chained ops | 2 + 3 + 4 chains correctly |
| Backspace | Remove last digit |

---

## How C++ ↔ Dart FFI works

1. `CMakeLists.txt` builds `libcalculator_native.so` via Android NDK
2. `calculator_jni.cpp` exports C functions with `extern "C"` + `__attribute__((visibility("default")))`
3. `calculator_ffi.dart` loads the `.so` via `DynamicLibrary.open()` and binds each function using `lookupFunction<>`
4. `CalculatorController` calls `CalculatorFFI` which calls into C++ for all math

---

## Test Results (C++ suite)

```
Results: 44 passed, 0 failed
```

All arithmetic ops, edge cases (div-by-zero, sqrt of negative), and expression
parsing (operator precedence, parentheses, sqrt function) verified ✅
