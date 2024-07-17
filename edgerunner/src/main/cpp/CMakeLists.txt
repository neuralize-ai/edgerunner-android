cmake_minimum_required(VERSION 3.22.1)

project("edgerunner")

add_library(model_jni SHARED
        model_jni.cpp)

add_library(tensor_jni SHARED
        tensor_jni.cpp)

find_package(edgerunner CONFIG)

target_link_libraries(model_jni PRIVATE edgerunner::edgerunner)
target_link_libraries(tensor_jni PRIVATE edgerunner::edgerunner)

set(RUNTIME_LIBS_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/arm64-v8a")
foreach(dir ${CONAN_RUNTIME_LIB_DIRS})
    file(GLOB_RECURSE runtimeLibs "${dir}/*.so")
    file(COPY ${runtimeLibs} DESTINATION ${RUNTIME_LIBS_DIR})
endforeach ()