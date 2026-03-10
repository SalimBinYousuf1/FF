// test/dart/calculator_widget_test.dart
//
// Widget tests for the Calculator UI.
// Run with: flutter test test/dart/calculator_widget_test.dart

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:calculator/ui/calc_button.dart';
import 'package:calculator/ui/calculator_screen.dart';
import 'package:calculator/calculator_controller.dart';

void main() {
  group('CalcButton widget', () {
    testWidgets('renders label', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: CalcButton(label: '5', onTap: () {}),
          ),
        ),
      );
      expect(find.text('5'), findsOneWidget);
    });

    testWidgets('calls onTap when pressed', (tester) async {
      bool tapped = false;
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: SizedBox(
              height: 80,
              width: 80,
              child: CalcButton(label: '7', onTap: () => tapped = true),
            ),
          ),
        ),
      );
      await tester.tap(find.text('7'));
      expect(tapped, true);
    });

    testWidgets('operator button has orange color', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: SizedBox(
              height: 80,
              width: 80,
              child: CalcButton(
                label: '+',
                onTap: () {},
                type: ButtonType.operator,
              ),
            ),
          ),
        ),
      );
      final material = tester.widget<Material>(
        find.byType(Material).last,
      );
      expect(material.color, const Color(0xFFFF9500));
    });

    testWidgets('function button has gray color', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: SizedBox(
              height: 80,
              width: 80,
              child: CalcButton(
                label: 'AC',
                onTap: () {},
                type: ButtonType.function,
              ),
            ),
          ),
        ),
      );
      final material = tester.widget<Material>(
        find.byType(Material).last,
      );
      expect(material.color, const Color(0xFF636366));
    });
  });

  group('CalculatorScreen layout', () {
    testWidgets('renders all expected buttons', (tester) async {
      late CalculatorController ctrl;
      try {
        ctrl = CalculatorController();
      } catch (_) {
        return; // Skip if FFI not available on host
      }

      await tester.pumpWidget(
        MaterialApp(
          home: CalculatorScreen(controller: ctrl),
        ),
      );

      // Verify all digit buttons present
      for (final digit in ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']) {
        expect(find.text(digit), findsOneWidget, reason: 'Button $digit missing');
      }

      // Verify operator buttons
      expect(find.text('+'), findsOneWidget);
      expect(find.text('−'), findsOneWidget);
      expect(find.text('×'), findsOneWidget);
      expect(find.text('÷'), findsOneWidget);
      expect(find.text('='), findsOneWidget);
      expect(find.text('AC'), findsOneWidget);
      expect(find.text('%'), findsOneWidget);
      expect(find.text('√'), findsOneWidget);
      expect(find.text('+/-'), findsOneWidget);
      expect(find.text('.'), findsOneWidget);
    });

    testWidgets('display shows initial 0', (tester) async {
      late CalculatorController ctrl;
      try {
        ctrl = CalculatorController();
      } catch (_) {
        return;
      }

      await tester.pumpWidget(
        MaterialApp(
          home: CalculatorScreen(controller: ctrl),
        ),
      );

      expect(find.text('0'), findsOneWidget);
    });
  });
}
