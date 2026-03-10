#include "calculator.h"
#include <stdexcept>
#include <cmath>
#include <cctype>
#include <sstream>

double Calculator::add(double a, double b) {
    return a + b;
}

double Calculator::subtract(double a, double b) {
    return a - b;
}

double Calculator::multiply(double a, double b) {
    return a * b;
}

double Calculator::divide(double a, double b) {
    if (b == 0.0) {
        throw std::runtime_error("Division by zero");
    }
    return a / b;
}

double Calculator::modulo(double a, double b) {
    if (b == 0.0) {
        throw std::runtime_error("Modulo by zero");
    }
    return std::fmod(a, b);
}

double Calculator::power(double a, double b) {
    return std::pow(a, b);
}

double Calculator::squareRoot(double a) {
    if (a < 0.0) {
        throw std::runtime_error("Square root of negative number");
    }
    return std::sqrt(a);
}

double Calculator::percentage(double a) {
    return a / 100.0;
}

// ---- Expression Parser (recursive descent) ----

void Calculator::skipWhitespace(const std::string& expr, size_t& pos) {
    while (pos < expr.size() && std::isspace(expr[pos])) {
        ++pos;
    }
}

double Calculator::parseNumber(const std::string& expr, size_t& pos) {
    skipWhitespace(expr, pos);
    size_t start = pos;
    bool hasDecimal = false;
    if (pos < expr.size() && (expr[pos] == '+' || expr[pos] == '-')) {
        ++pos;
    }
    while (pos < expr.size() && (std::isdigit(expr[pos]) || expr[pos] == '.')) {
        if (expr[pos] == '.') {
            if (hasDecimal) throw std::runtime_error("Invalid number");
            hasDecimal = true;
        }
        ++pos;
    }
    if (pos == start) throw std::runtime_error("Expected number");
    return std::stod(expr.substr(start, pos - start));
}

double Calculator::parseFactor(const std::string& expr, size_t& pos) {
    skipWhitespace(expr, pos);
    if (pos < expr.size() && expr[pos] == '(') {
        ++pos; // consume '('
        double val = parseExpression(expr, pos);
        skipWhitespace(expr, pos);
        if (pos >= expr.size() || expr[pos] != ')') {
            throw std::runtime_error("Missing closing parenthesis");
        }
        ++pos; // consume ')'
        return val;
    }
    // Handle unary minus
    if (pos < expr.size() && expr[pos] == '-') {
        ++pos;
        return -parseFactor(expr, pos);
    }
    // Handle sqrt function
    if (expr.substr(pos, 4) == "sqrt") {
        pos += 4;
        skipWhitespace(expr, pos);
        if (pos < expr.size() && expr[pos] == '(') {
            ++pos;
            double val = parseExpression(expr, pos);
            skipWhitespace(expr, pos);
            if (pos >= expr.size() || expr[pos] != ')') {
                throw std::runtime_error("Missing closing parenthesis for sqrt");
            }
            ++pos;
            return squareRoot(val);
        }
    }
    return parseNumber(expr, pos);
}

double Calculator::parseTerm(const std::string& expr, size_t& pos) {
    double left = parseFactor(expr, pos);
    skipWhitespace(expr, pos);
    while (pos < expr.size() && (expr[pos] == '*' || expr[pos] == '/' || expr[pos] == '%')) {
        char op = expr[pos++];
        double right = parseFactor(expr, pos);
        skipWhitespace(expr, pos);
        if (op == '*') left = multiply(left, right);
        else if (op == '/') left = divide(left, right);
        else if (op == '%') left = modulo(left, right);
    }
    return left;
}

double Calculator::parseExpression(const std::string& expr, size_t& pos) {
    double left = parseTerm(expr, pos);
    skipWhitespace(expr, pos);
    while (pos < expr.size() && (expr[pos] == '+' || expr[pos] == '-')) {
        char op = expr[pos++];
        double right = parseTerm(expr, pos);
        skipWhitespace(expr, pos);
        if (op == '+') left = add(left, right);
        else if (op == '-') left = subtract(left, right);
    }
    return left;
}

double Calculator::evaluate(const std::string& expression) {
    if (expression.empty()) {
        throw std::runtime_error("Empty expression");
    }
    size_t pos = 0;
    double result = parseExpression(expression, pos);
    skipWhitespace(expression, pos);
    if (pos != expression.size()) {
        throw std::runtime_error("Unexpected character at position " + std::to_string(pos));
    }
    return result;
}
