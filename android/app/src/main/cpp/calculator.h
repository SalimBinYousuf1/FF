#ifndef CALCULATOR_H
#define CALCULATOR_H

#include <string>
#include <stdexcept>
#include <cmath>

class Calculator {
public:
    // Basic arithmetic
    static double add(double a, double b);
    static double subtract(double a, double b);
    static double multiply(double a, double b);
    static double divide(double a, double b);

    // Advanced operations
    static double modulo(double a, double b);
    static double power(double a, double b);
    static double squareRoot(double a);
    static double percentage(double a);

    // Expression evaluation
    static double evaluate(const std::string& expression);

private:
    static double parseExpression(const std::string& expr, size_t& pos);
    static double parseTerm(const std::string& expr, size_t& pos);
    static double parseFactor(const std::string& expr, size_t& pos);
    static double parseNumber(const std::string& expr, size_t& pos);
    static void skipWhitespace(const std::string& expr, size_t& pos);
};

#endif // CALCULATOR_H
