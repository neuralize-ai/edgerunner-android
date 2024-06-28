cmake_minimum_required(VERSION 3.22.1)

project("edgerunner")

add_library(edgerunner_jni SHARED
        edgerunner_jni.cpp)

find_package(edgerunner CONFIG)
target_link_libraries(edgerunner_jni edgerunner::edgerunner)