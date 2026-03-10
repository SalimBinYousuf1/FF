package com.example.calculator;

/**
 * Java bridge to the native C++ calculator library.
 * Used as fallback/alternative to Flutter FFI.
 */
public class CalcBridge {

    static {
        System.loadLibrary("calculator_native");
    }

    /**
     * Evaluate a full math expression string using the C++ engine.
     * Returns Double.NaN on error.
     */
    public native double evaluate(String expression);
}
