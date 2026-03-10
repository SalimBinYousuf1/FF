// lib/native/calculator_ffi.dart
//
// Dart FFI bindings to the native C++ calculator_native shared library.
// Calls the C functions exported in calculator_jni.cpp.

import 'dart:ffi';
import 'dart:io';
import 'package:ffi/ffi.dart';

// ── C function signatures ──────────────────────────────────────────────────

// double calc_evaluate(const char* expression, char* out_error, int error_len)
typedef _CalcEvaluateC = Double Function(
    Pointer<Utf8> expression, Pointer<Utf8> outError, Int32 errorLen);
typedef _CalcEvaluateDart = double Function(
    Pointer<Utf8> expression, Pointer<Utf8> outError, int errorLen);

// double calc_add(double a, double b)
typedef _CalcBinaryC = Double Function(Double a, Double b);
typedef _CalcBinaryDart = double Function(double a, double b);

// double calc_divide(double a, double b, char* out_error, int error_len)
typedef _CalcDivideC = Double Function(
    Double a, Double b, Pointer<Utf8> outError, Int32 errorLen);
typedef _CalcDivideDart = double Function(
    double a, double b, Pointer<Utf8> outError, int errorLen);

// double calc_sqrt(double a, char* out_error, int error_len)
typedef _CalcSqrtC = Double Function(
    Double a, Pointer<Utf8> outError, Int32 errorLen);
typedef _CalcSqrtDart = double Function(
    double a, Pointer<Utf8> outError, int errorLen);

// double calc_percentage(double a)
typedef _CalcUnaryC = Double Function(Double a);
typedef _CalcUnaryDart = double Function(double a);

// ── Library loader ─────────────────────────────────────────────────────────

DynamicLibrary _loadLib() {
  if (Platform.isAndroid) {
    return DynamicLibrary.open('libcalculator_native.so');
  }
  if (Platform.isLinux) {
    return DynamicLibrary.open('libcalculator_native.so');
  }
  if (Platform.isMacOS) {
    return DynamicLibrary.open('libcalculator_native.dylib');
  }
  if (Platform.isWindows) {
    return DynamicLibrary.open('calculator_native.dll');
  }
  throw UnsupportedError('Unsupported platform: ${Platform.operatingSystem}');
}

// ── Public API ─────────────────────────────────────────────────────────────

/// Wraps the C++ Calculator class via Dart FFI.
class CalculatorFFI {
  late final _CalcEvaluateDart _evaluate;
  late final _CalcBinaryDart _add;
  late final _CalcBinaryDart _subtract;
  late final _CalcBinaryDart _multiply;
  late final _CalcDivideDart _divide;
  late final _CalcSqrtDart _sqrt;
  late final _CalcUnaryDart _percentage;
  late final _CalcBinaryDart _power;

  static CalculatorFFI? _instance;

  CalculatorFFI._internal() {
    final lib = _loadLib();
    _evaluate =
        lib.lookupFunction<_CalcEvaluateC, _CalcEvaluateDart>('calc_evaluate');
    _add = lib.lookupFunction<_CalcBinaryC, _CalcBinaryDart>('calc_add');
    _subtract =
        lib.lookupFunction<_CalcBinaryC, _CalcBinaryDart>('calc_subtract');
    _multiply =
        lib.lookupFunction<_CalcBinaryC, _CalcBinaryDart>('calc_multiply');
    _divide =
        lib.lookupFunction<_CalcDivideC, _CalcDivideDart>('calc_divide');
    _sqrt = lib.lookupFunction<_CalcSqrtC, _CalcSqrtDart>('calc_sqrt');
    _percentage =
        lib.lookupFunction<_CalcUnaryC, _CalcUnaryDart>('calc_percentage');
    _power = lib.lookupFunction<_CalcBinaryC, _CalcBinaryDart>('calc_power');
  }

  factory CalculatorFFI() {
    _instance ??= CalculatorFFI._internal();
    return _instance!;
  }

  static const int _errorBufLen = 256;

  /// Evaluate a full math expression. Throws [CalculatorException] on error.
  double evaluate(String expression) {
    final exprPtr = expression.toNativeUtf8();
    final errPtr = calloc<Utf8>(_errorBufLen);
    try {
      final result = _evaluate(exprPtr, errPtr, _errorBufLen);
      if (result.isNaN) {
        final msg = errPtr.toDartString();
        throw CalculatorException(msg.isEmpty ? 'Unknown error' : msg);
      }
      return result;
    } finally {
      calloc.free(exprPtr);
      calloc.free(errPtr);
    }
  }

  double add(double a, double b) => _add(a, b);
  double subtract(double a, double b) => _subtract(a, b);
  double multiply(double a, double b) => _multiply(a, b);

  double divide(double a, double b) {
    final errPtr = calloc<Utf8>(_errorBufLen);
    try {
      final result = _divide(a, b, errPtr, _errorBufLen);
      if (result.isNaN) {
        final msg = errPtr.toDartString();
        throw CalculatorException(msg.isEmpty ? 'Division error' : msg);
      }
      return result;
    } finally {
      calloc.free(errPtr);
    }
  }

  double sqrt(double a) {
    final errPtr = calloc<Utf8>(_errorBufLen);
    try {
      final result = _sqrt(a, errPtr, _errorBufLen);
      if (result.isNaN) {
        final msg = errPtr.toDartString();
        throw CalculatorException(msg.isEmpty ? 'Sqrt error' : msg);
      }
      return result;
    } finally {
      calloc.free(errPtr);
    }
  }

  double percentage(double a) => _percentage(a);
  double power(double a, double b) => _power(a, b);
}

class CalculatorException implements Exception {
  final String message;
  CalculatorException(this.message);

  @override
  String toString() => 'CalculatorException: $message';
}
