#include "calculator.h"
#include <jni.h>
#include <string>
#include <cstring>
#include <android/log.h>

#define LOG_TAG "CalculatorJNI"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// -------- FFI exports for Flutter Dart FFI --------
// These are called directly by Dart via dart:ffi

extern "C" {

/**
 * Evaluate a math expression string.
 * Returns the result as a double.
 * On error, returns NaN and writes error message to out_error (if non-null, max 256 chars).
 */
__attribute__((visibility("default"))) __attribute__((used))
double calc_evaluate(const char* expression, char* out_error, int error_len) {
    try {
        std::string expr(expression);
        double result = Calculator::evaluate(expr);
        if (out_error && error_len > 0) out_error[0] = '\0';
        return result;
    } catch (const std::exception& e) {
        if (out_error && error_len > 0) {
            strncpy(out_error, e.what(), error_len - 1);
            out_error[error_len - 1] = '\0';
        }
        return std::numeric_limits<double>::quiet_NaN();
    }
}

__attribute__((visibility("default"))) __attribute__((used))
double calc_add(double a, double b) {
    return Calculator::add(a, b);
}

__attribute__((visibility("default"))) __attribute__((used))
double calc_subtract(double a, double b) {
    return Calculator::subtract(a, b);
}

__attribute__((visibility("default"))) __attribute__((used))
double calc_multiply(double a, double b) {
    return Calculator::multiply(a, b);
}

__attribute__((visibility("default"))) __attribute__((used))
double calc_divide(double a, double b, char* out_error, int error_len) {
    try {
        return Calculator::divide(a, b);
    } catch (const std::exception& e) {
        if (out_error && error_len > 0) {
            strncpy(out_error, e.what(), error_len - 1);
            out_error[error_len - 1] = '\0';
        }
        return std::numeric_limits<double>::quiet_NaN();
    }
}

__attribute__((visibility("default"))) __attribute__((used))
double calc_sqrt(double a, char* out_error, int error_len) {
    try {
        return Calculator::squareRoot(a);
    } catch (const std::exception& e) {
        if (out_error && error_len > 0) {
            strncpy(out_error, e.what(), error_len - 1);
            out_error[error_len - 1] = '\0';
        }
        return std::numeric_limits<double>::quiet_NaN();
    }
}

__attribute__((visibility("default"))) __attribute__((used))
double calc_percentage(double a) {
    return Calculator::percentage(a);
}

__attribute__((visibility("default"))) __attribute__((used))
double calc_power(double a, double b) {
    return Calculator::power(a, b);
}

// -------- JNI bridge (for Android Java/Kotlin interop if needed) --------

extern "C" JNIEXPORT jdouble JNICALL
Java_com_example_calculator_CalcBridge_evaluate(
        JNIEnv* env, jobject /* this */, jstring expression) {
    const char* expr_cstr = env->GetStringUTFChars(expression, nullptr);
    double result = 0.0;
    try {
        result = Calculator::evaluate(std::string(expr_cstr));
    } catch (const std::exception& e) {
        LOGE("evaluate error: %s", e.what());
        result = std::numeric_limits<double>::quiet_NaN();
    }
    env->ReleaseStringUTFChars(expression, expr_cstr);
    return result;
}

} // extern "C"
