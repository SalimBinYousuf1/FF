// test/cpp/calculator_test.cpp
// Standalone C++ unit tests — no external frameworks needed.
// Build & run: g++ -std=c++17 calculator_test.cpp ../android/app/src/main/cpp/calculator.cpp -o calc_test && ./calc_test

#include <iostream>
#include <cassert>
#include <cmath>
#include <stdexcept>
#include <string>
#include "../../android/app/src/main/cpp/calculator.h"

static int passed = 0;
static int failed = 0;

#define ASSERT_EQ(got, expected, name) do { \
    double _g = (got), _e = (expected); \
    if (std::abs(_g - _e) < 1e-9) { \
        std::cout << "[PASS] " << name << "\n"; \
        ++passed; \
    } else { \
        std::cout << "[FAIL] " << name << "  got=" << _g << "  expected=" << _e << "\n"; \
        ++failed; \
    } \
} while(0)

#define ASSERT_THROWS(expr, name) do { \
    bool threw = false; \
    try { expr; } catch (...) { threw = true; } \
    if (threw) { \
        std::cout << "[PASS] " << name << " (throws as expected)\n"; \
        ++passed; \
    } else { \
        std::cout << "[FAIL] " << name << " (expected throw but none)\n"; \
        ++failed; \
    } \
} while(0)

// ── Basic arithmetic ───────────────────────────────────────────────────────

void testAdd() {
    ASSERT_EQ(Calculator::add(2, 3), 5, "add: 2+3=5");
    ASSERT_EQ(Calculator::add(-1, 1), 0, "add: -1+1=0");
    ASSERT_EQ(Calculator::add(0, 0), 0, "add: 0+0=0");
    ASSERT_EQ(Calculator::add(1.5, 2.5), 4.0, "add: 1.5+2.5=4");
    ASSERT_EQ(Calculator::add(-5.5, -4.5), -10.0, "add: -5.5+-4.5=-10");
}

void testSubtract() {
    ASSERT_EQ(Calculator::subtract(10, 4), 6, "sub: 10-4=6");
    ASSERT_EQ(Calculator::subtract(0, 5), -5, "sub: 0-5=-5");
    ASSERT_EQ(Calculator::subtract(-3, -3), 0, "sub: -3-(-3)=0");
}

void testMultiply() {
    ASSERT_EQ(Calculator::multiply(3, 4), 12, "mul: 3*4=12");
    ASSERT_EQ(Calculator::multiply(-2, 5), -10, "mul: -2*5=-10");
    ASSERT_EQ(Calculator::multiply(0, 999), 0, "mul: 0*999=0");
    ASSERT_EQ(Calculator::multiply(2.5, 4), 10.0, "mul: 2.5*4=10");
}

void testDivide() {
    ASSERT_EQ(Calculator::divide(10, 2), 5, "div: 10/2=5");
    ASSERT_EQ(Calculator::divide(7, 2), 3.5, "div: 7/2=3.5");
    ASSERT_EQ(Calculator::divide(-6, 3), -2, "div: -6/3=-2");
    ASSERT_THROWS(Calculator::divide(1, 0), "div: 1/0 throws");
}

void testModulo() {
    ASSERT_EQ(Calculator::modulo(10, 3), 1, "mod: 10%3=1");
    ASSERT_EQ(Calculator::modulo(9, 3), 0, "mod: 9%3=0");
    ASSERT_THROWS(Calculator::modulo(5, 0), "mod: 5%0 throws");
}

void testPower() {
    ASSERT_EQ(Calculator::power(2, 10), 1024, "pow: 2^10=1024");
    ASSERT_EQ(Calculator::power(5, 0), 1, "pow: 5^0=1");
    ASSERT_EQ(Calculator::power(9, 0.5), 3, "pow: 9^0.5=3");
}

void testSqrt() {
    ASSERT_EQ(Calculator::squareRoot(9), 3, "sqrt: sqrt(9)=3");
    ASSERT_EQ(Calculator::squareRoot(2), std::sqrt(2.0), "sqrt: sqrt(2)");
    ASSERT_EQ(Calculator::squareRoot(0), 0, "sqrt: sqrt(0)=0");
    ASSERT_THROWS(Calculator::squareRoot(-1), "sqrt: sqrt(-1) throws");
}

void testPercentage() {
    ASSERT_EQ(Calculator::percentage(50), 0.5, "pct: 50%=0.5");
    ASSERT_EQ(Calculator::percentage(100), 1.0, "pct: 100%=1.0");
    ASSERT_EQ(Calculator::percentage(0), 0.0, "pct: 0%=0.0");
}

// ── Expression evaluator ──────────────────────────────────────────────────

void testEvaluate() {
    ASSERT_EQ(Calculator::evaluate("2+3"), 5, "eval: 2+3=5");
    ASSERT_EQ(Calculator::evaluate("10-4"), 6, "eval: 10-4=6");
    ASSERT_EQ(Calculator::evaluate("3*4"), 12, "eval: 3*4=12");
    ASSERT_EQ(Calculator::evaluate("10/2"), 5, "eval: 10/2=5");
    ASSERT_EQ(Calculator::evaluate("2+3*4"), 14, "eval: 2+3*4=14 (precedence)");
    ASSERT_EQ(Calculator::evaluate("(2+3)*4"), 20, "eval: (2+3)*4=20");
    ASSERT_EQ(Calculator::evaluate("10/2+3*4"), 17, "eval: 10/2+3*4=17");
    ASSERT_EQ(Calculator::evaluate("sqrt(16)"), 4, "eval: sqrt(16)=4");
    ASSERT_EQ(Calculator::evaluate("2+sqrt(9)"), 5, "eval: 2+sqrt(9)=5");
    ASSERT_EQ(Calculator::evaluate("  3 + 4 "), 7, "eval: spaces handled");
    ASSERT_EQ(Calculator::evaluate("1.5+2.5"), 4.0, "eval: floats");
    ASSERT_THROWS(Calculator::evaluate("1/0"), "eval: div by zero throws");
    ASSERT_THROWS(Calculator::evaluate("sqrt(-4)"), "eval: sqrt neg throws");
    ASSERT_THROWS(Calculator::evaluate(""), "eval: empty throws");
    ASSERT_THROWS(Calculator::evaluate("2+*3"), "eval: bad expr throws");
}

// ── Main ──────────────────────────────────────────────────────────────────

int main() {
    std::cout << "======= Calculator C++ Unit Tests =======\n\n";

    testAdd();
    testSubtract();
    testMultiply();
    testDivide();
    testModulo();
    testPower();
    testSqrt();
    testPercentage();
    testEvaluate();

    std::cout << "\n=========================================\n";
    std::cout << "Results: " << passed << " passed, " << failed << " failed\n";
    return (failed > 0) ? 1 : 0;
}
