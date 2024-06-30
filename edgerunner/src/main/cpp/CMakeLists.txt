cmake_minimum_required(VERSION 3.22.1)

project("edgerunner")

add_library(model_jni SHARED
        model_jni.cpp)

add_library(tensor_jni SHARED
        tensor_jni.cpp)

find_package(edgerunner CONFIG)

target_link_libraries(model_jni edgerunner::edgerunner)
target_link_libraries(tensor_jni edgerunner::edgerunner)
