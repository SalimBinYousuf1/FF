// lib/ui/calculator_screen.dart

import 'package:flutter/material.dart';
import '../calculator_controller.dart';
import 'calc_button.dart';

class CalculatorScreen extends StatefulWidget {
  final CalculatorController controller;

  const CalculatorScreen({super.key, required this.controller});

  @override
  State<CalculatorScreen> createState() => _CalculatorScreenState();
}

class _CalculatorScreenState extends State<CalculatorScreen> {
  CalculatorController get ctrl => widget.controller;

  @override
  void initState() {
    super.initState();
    ctrl.addListener(_onControllerChange);
  }

  @override
  void dispose() {
    ctrl.removeListener(_onControllerChange);
    super.dispose();
  }

  void _onControllerChange() => setState(() {});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: Column(
          children: [
            // ── Display ──────────────────────────────────────────────
            Expanded(
              flex: 3,
              child: _DisplayPanel(ctrl: ctrl),
            ),
            // ── Button grid ──────────────────────────────────────────
            Expanded(
              flex: 5,
              child: _ButtonGrid(ctrl: ctrl),
            ),
          ],
        ),
      ),
    );
  }
}

// ── Display Panel ──────────────────────────────────────────────────────────

class _DisplayPanel extends StatelessWidget {
  final CalculatorController ctrl;
  const _DisplayPanel({required this.ctrl});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          // Expression / history row
          if (ctrl.expression.isNotEmpty)
            Text(
              ctrl.expression,
              style: const TextStyle(
                color: Color(0xFF8E8E93),
                fontSize: 18,
                fontWeight: FontWeight.w300,
              ),
              textAlign: TextAlign.right,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
          const SizedBox(height: 8),
          // Main display
          FittedBox(
            fit: BoxFit.scaleDown,
            alignment: Alignment.centerRight,
            child: Text(
              ctrl.display,
              style: TextStyle(
                color: ctrl.hasError ? Colors.red[300] : Colors.white,
                fontSize: 72,
                fontWeight: FontWeight.w200,
                letterSpacing: -2,
              ),
              textAlign: TextAlign.right,
            ),
          ),
          const SizedBox(height: 8),
        ],
      ),
    );
  }
}

// ── Button Grid ────────────────────────────────────────────────────────────

class _ButtonGrid extends StatelessWidget {
  final CalculatorController ctrl;
  const _ButtonGrid({required this.ctrl});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      child: Column(
        children: [
          // Row 1: AC, +/-, %, ÷
          _buildRow([
            CalcButton(
              label: ctrl.display == '0' || ctrl.hasError ? 'AC' : 'C',
              type: ButtonType.function,
              onTap: () => ctrl.onClear(),
            ),
            CalcButton(
              label: '+/-',
              type: ButtonType.function,
              onTap: () => ctrl.onToggleSign(),
            ),
            CalcButton(
              label: '%',
              type: ButtonType.function,
              onTap: () => ctrl.onPercentage(),
            ),
            CalcButton(
              label: '÷',
              type: ButtonType.operator,
              onTap: () => ctrl.onOperation(CalcOp.divide),
            ),
          ]),
          // Row 2: 7, 8, 9, ×
          _buildRow([
            CalcButton(label: '7', onTap: () => ctrl.onDigit('7')),
            CalcButton(label: '8', onTap: () => ctrl.onDigit('8')),
            CalcButton(label: '9', onTap: () => ctrl.onDigit('9')),
            CalcButton(
              label: '×',
              type: ButtonType.operator,
              onTap: () => ctrl.onOperation(CalcOp.multiply),
            ),
          ]),
          // Row 3: 4, 5, 6, −
          _buildRow([
            CalcButton(label: '4', onTap: () => ctrl.onDigit('4')),
            CalcButton(label: '5', onTap: () => ctrl.onDigit('5')),
            CalcButton(label: '6', onTap: () => ctrl.onDigit('6')),
            CalcButton(
              label: '−',
              type: ButtonType.operator,
              onTap: () => ctrl.onOperation(CalcOp.subtract),
            ),
          ]),
          // Row 4: 1, 2, 3, +
          _buildRow([
            CalcButton(label: '1', onTap: () => ctrl.onDigit('1')),
            CalcButton(label: '2', onTap: () => ctrl.onDigit('2')),
            CalcButton(label: '3', onTap: () => ctrl.onDigit('3')),
            CalcButton(
              label: '+',
              type: ButtonType.operator,
              onTap: () => ctrl.onOperation(CalcOp.add),
            ),
          ]),
          // Row 5: √, 0, ., =
          _buildRow([
            CalcButton(
              label: '√',
              type: ButtonType.function,
              onTap: () => ctrl.onSqrt(),
            ),
            CalcButton(label: '0', onTap: () => ctrl.onDigit('0')),
            CalcButton(label: '.', onTap: () => ctrl.onDecimal()),
            CalcButton(
              label: '=',
              type: ButtonType.operator,
              onTap: () => ctrl.onEquals(),
            ),
          ]),
        ],
      ),
    );
  }

  Widget _buildRow(List<Widget> buttons) {
    return Expanded(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: buttons
            .map((b) => Expanded(child: b))
            .toList(),
      ),
    );
  }
}
