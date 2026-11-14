cmake_minimum_required(VERSION 3.22.1)
project(castaway_engine LANGUAGES CXX)

add_library(castaway_engine SHARED native_engine.cpp)

target_compile_features(castaway_engine PRIVATE cxx_std_20)
target_compile_options(castaway_engine PRIVATE -Wall -Wextra -Werror)
target_link_libraries(castaway_engine PRIVATE log android)
