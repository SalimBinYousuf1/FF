// lib/ui/calc_button.dart

import 'package:flutter/material.dart';

enum ButtonType { digit, operator, function }

class CalcButton extends StatelessWidget {
  final String label;
  final VoidCallback onTap;
  final ButtonType type;

  const CalcButton({
    super.key,
    required this.label,
    required this.onTap,
    this.type = ButtonType.digit,
  });

  Color get _backgroundColor {
    switch (type) {
      case ButtonType.operator:
        return const Color(0xFFFF9500); // iOS-style orange
      case ButtonType.function:
        return const Color(0xFF636366); // Gray
      case ButtonType.digit:
        return const Color(0xFF2C2C2E); // Dark
    }
  }

  Color get _textColor {
    switch (type) {
      case ButtonType.operator:
        return Colors.white;
      case ButtonType.function:
        return Colors.white;
      case ButtonType.digit:
        return Colors.white;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(6),
      child: Material(
        color: _backgroundColor,
        borderRadius: BorderRadius.circular(100),
        child: InkWell(
          borderRadius: BorderRadius.circular(100),
          splashColor: Colors.white24,
          highlightColor: Colors.white12,
          onTap: onTap,
          child: Center(
            child: Text(
              label,
              style: TextStyle(
                color: _textColor,
                fontSize: 28,
                fontWeight: FontWeight.w400,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
