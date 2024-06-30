#include "model_jni.hpp"

extern "C" {

JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Model_nativeCreate(
    JNIEnv *env, jobject obj, jstring modelPath) {
  const char *nativeModelPath = env->GetStringUTFChars(modelPath, 0);
  JniModel *model = new JniModel(nativeModelPath);
  env->ReleaseStringUTFChars(modelPath, nativeModelPath);
  return reinterpret_cast<jlong>(model);
}

JNIEXPORT jstring JNICALL Java_com_neuralize_edgerunner_Model_nativeGetName(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  return env->NewStringUTF(model->getName().c_str());
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeGetNumInputs(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  return static_cast<jint>(model->getNumInputs());
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeGetNumOutputs(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  return static_cast<jint>(model->getNumOutputs());
}

JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Model_nativeGetInput(
    JNIEnv *env, jobject obj, jlong modelPtr, jint index) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->getInput(env, static_cast<size_t>(index));
}

JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Model_nativeGetOutput(
    JNIEnv *env, jobject obj, jlong modelPtr, jint index) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->getOutput(env, static_cast<size_t>(index));
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeGetDelegate(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->getDelegate();
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeApplyDelegate(
    JNIEnv *env, jobject obj, jlong modelPtr, jint delegate) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->applyDelegate(static_cast<int>(delegate));
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeExecute(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->execute();
}

JNIEXPORT void JNICALL Java_com_neuralize_edgerunner_Model_nativeDestroy(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  JniModel *model = reinterpret_cast<JniModel *>(modelPtr);
  delete model;
}
}
