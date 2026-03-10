// lib/main.dart

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'calculator_controller.dart';
import 'ui/calculator_screen.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);
  runApp(const CalculatorApp());
}

class CalculatorApp extends StatelessWidget {
  const CalculatorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Calculator',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFFFF9500),
          surface: Color(0xFF1C1C1E),
          background: Color(0xFF000000),
        ),
        useMaterial3: true,
      ),
      home: CalculatorScreen(controller: CalculatorController()),
    );
  }
}
