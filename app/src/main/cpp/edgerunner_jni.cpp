#include "edgerunner/edgerunner.hpp"
#include "edgerunner/model.hpp"
#include <jni.h>

extern "C" {
JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Edgerunner_createModel(
    JNIEnv *env, jobject obj, jstring modelPath) {
  const char *path = env->GetStringUTFChars(modelPath, nullptr);
  auto model = edge::createModel(std::filesystem::path(path));
  env->ReleaseStringUTFChars(modelPath, path);
  return reinterpret_cast<jlong>(model.release());
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Edgerunner_applyDelegate(
    JNIEnv *env, jobject obj, jlong modelPtr, jint delegate) {
  auto model = reinterpret_cast<edge::Model *>(modelPtr);
  return static_cast<jint>(
      model->applyDelegate(static_cast<edge::DELEGATE>(delegate)));
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Edgerunner_execute(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto model = reinterpret_cast<edge::Model *>(modelPtr);
  return static_cast<jint>(model->execute());
}

JNIEXPORT jstring JNICALL Java_com_neuralize_edgerunner_Edgerunner_getModelName(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto model = reinterpret_cast<edge::Model *>(modelPtr);
  return env->NewStringUTF(model->name().c_str());
}

JNIEXPORT void JNICALL Java_com_neuralize_edgerunner_Edgerunner_deleteModel(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto model = reinterpret_cast<edge::Model *>(modelPtr);
  delete model;
}
}
