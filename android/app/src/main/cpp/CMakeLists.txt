cmake_minimum_required(VERSION 3.18.1)
project(calculator_native VERSION 1.0.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Build the shared library
add_library(
    calculator_native
    SHARED
    calculator.cpp
    calculator_jni.cpp
)

# Include headers
target_include_directories(calculator_native PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

# Link Android log library
find_library(log-lib log)
target_link_libraries(calculator_native ${log-lib})

# Optimization flags
target_compile_options(calculator_native PRIVATE
    -O2
    -fvisibility=default
    -Wall
    -Wextra
)
