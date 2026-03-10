// lib/calculator_controller.dart
//
// Business logic layer. Manages expression building and delegates
// actual computation to the C++ engine via FFI.

import 'package:flutter/foundation.dart';
import 'native/calculator_ffi.dart';

enum CalcOp { add, subtract, multiply, divide, power, none }

class CalculatorController extends ChangeNotifier {
  final CalculatorFFI _ffi = CalculatorFFI();

  String _display = '0';
  String _expression = '';
  double? _storedValue;
  CalcOp _pendingOp = CalcOp.none;
  bool _startNewInput = true;
  bool _hasError = false;
  String _fullExpression = ''; // shown in the history bar

  // ── Getters ────────────────────────────────────────────────────────────

  String get display => _display;
  String get expression => _fullExpression;
  bool get hasError => _hasError;

  // ── Input handlers ────────────────────────────────────────────────────

  void onDigit(String digit) {
    _clearErrorIfNeeded();
    if (_startNewInput) {
      _display = digit;
      _startNewInput = false;
    } else {
      if (_display == '0' && digit != '.') {
        _display = digit;
      } else {
        if (digit == '.' && _display.contains('.')) return;
        if (_display.length < 15) _display += digit;
      }
    }
    notifyListeners();
  }

  void onDecimal() => onDigit('.');

  void onOperation(CalcOp op) {
    _clearErrorIfNeeded();
    final current = _parseDisplay();
    if (current == null) return;

    if (_storedValue != null && !_startNewInput) {
      // Chain calculation
      _computePending(current);
    } else {
      _storedValue = current;
    }

    _pendingOp = op;
    _fullExpression = '${_formatNumber(_storedValue!)} ${_opSymbol(op)}';
    _startNewInput = true;
    notifyListeners();
  }

  void onEquals() {
    _clearErrorIfNeeded();
    if (_pendingOp == CalcOp.none || _storedValue == null) return;
    final current = _parseDisplay();
    if (current == null) return;

    final a = _storedValue!;
    final b = current;
    _fullExpression =
        '${_formatNumber(a)} ${_opSymbol(_pendingOp)} ${_formatNumber(b)} =';

    try {
      final result = _computeOp(_pendingOp, a, b);
      _display = _formatNumber(result);
      _storedValue = result;
    } catch (e) {
      _setError(e is CalculatorException ? e.message : 'Error');
      return;
    }

    _pendingOp = CalcOp.none;
    _startNewInput = true;
    notifyListeners();
  }

  void onSqrt() {
    _clearErrorIfNeeded();
    final val = _parseDisplay();
    if (val == null) return;
    _fullExpression = '√(${_formatNumber(val)})';
    try {
      final result = _ffi.sqrt(val);
      _display = _formatNumber(result);
      _storedValue = result;
      _startNewInput = true;
    } catch (e) {
      _setError(e is CalculatorException ? e.message : 'Error');
      return;
    }
    notifyListeners();
  }

  void onPercentage() {
    _clearErrorIfNeeded();
    final val = _parseDisplay();
    if (val == null) return;
    final result = _ffi.percentage(val);
    _display = _formatNumber(result);
    _fullExpression = '${_formatNumber(val)}%';
    _startNewInput = true;
    notifyListeners();
  }

  void onToggleSign() {
    _clearErrorIfNeeded();
    final val = _parseDisplay();
    if (val == null) return;
    _display = _formatNumber(-val);
    notifyListeners();
  }

  /// Evaluate a raw expression string via the C++ parser
  void onEvaluateExpression(String expr) {
    _clearErrorIfNeeded();
    try {
      final result = _ffi.evaluate(expr);
      _display = _formatNumber(result);
      _fullExpression = '$expr =';
      _storedValue = result;
      _startNewInput = true;
    } catch (e) {
      _setError(e is CalculatorException ? e.message : 'Error');
    }
    notifyListeners();
  }

  void onClear() {
    _display = '0';
    _expression = '';
    _fullExpression = '';
    _storedValue = null;
    _pendingOp = CalcOp.none;
    _startNewInput = true;
    _hasError = false;
    notifyListeners();
  }

  void onBackspace() {
    if (_startNewInput || _hasError) {
      onClear();
      return;
    }
    if (_display.length <= 1 || (_display.length == 2 && _display.startsWith('-'))) {
      _display = '0';
      _startNewInput = true;
    } else {
      _display = _display.substring(0, _display.length - 1);
    }
    notifyListeners();
  }

  // ── Private helpers ───────────────────────────────────────────────────

  double? _parseDisplay() {
    return double.tryParse(_display);
  }

  void _computePending(double current) {
    try {
      _storedValue = _computeOp(_pendingOp, _storedValue!, current);
      _display = _formatNumber(_storedValue!);
    } catch (e) {
      _setError(e is CalculatorException ? e.message : 'Error');
    }
  }

  double _computeOp(CalcOp op, double a, double b) {
    switch (op) {
      case CalcOp.add:
        return _ffi.add(a, b);
      case CalcOp.subtract:
        return _ffi.subtract(a, b);
      case CalcOp.multiply:
        return _ffi.multiply(a, b);
      case CalcOp.divide:
        return _ffi.divide(a, b);
      case CalcOp.power:
        return _ffi.power(a, b);
      case CalcOp.none:
        return b;
    }
  }

  String _opSymbol(CalcOp op) {
    switch (op) {
      case CalcOp.add:
        return '+';
      case CalcOp.subtract:
        return '−';
      case CalcOp.multiply:
        return '×';
      case CalcOp.divide:
        return '÷';
      case CalcOp.power:
        return '^';
      case CalcOp.none:
        return '';
    }
  }

  String _formatNumber(double val) {
    if (val.isNaN) return 'Error';
    if (val.isInfinite) return val > 0 ? '∞' : '-∞';
    // Show as int if whole number
    if (val == val.truncateToDouble() && val.abs() < 1e15) {
      return val.toInt().toString();
    }
    // Trim trailing zeros
    String s = val.toStringAsPrecision(10);
    if (s.contains('.')) {
      s = s.replaceAll(RegExp(r'0+$'), '').replaceAll(RegExp(r'\.$'), '');
    }
    return s;
  }

  void _setError(String msg) {
    _display = msg;
    _hasError = true;
    _storedValue = null;
    _pendingOp = CalcOp.none;
    _startNewInput = true;
    notifyListeners();
  }

  void _clearErrorIfNeeded() {
    if (_hasError) {
      _hasError = false;
      _display = '0';
    }
  }
}
