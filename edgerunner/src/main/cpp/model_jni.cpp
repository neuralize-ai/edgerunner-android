#include "model_jni.hpp"

extern "C" {

JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Model_nativeCreate(
    JNIEnv *env, jobject obj, jstring modelPath) {
  const char *nativeModelPath = env->GetStringUTFChars(modelPath, nullptr);
  auto *model = new JniModel(nativeModelPath);
  env->ReleaseStringUTFChars(modelPath, nativeModelPath);
  return reinterpret_cast<jlong>(model);
}

JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Model_nativeCreateFromBuffer(
    JNIEnv *env, jobject obj, jobject byteBuffer) {
  auto *buffer =
      static_cast<uint8_t *>(env->GetDirectBufferAddress(byteBuffer));
  jlong capacity = env->GetDirectBufferCapacity(byteBuffer);
  nonstd::span<uint8_t> modelData(buffer, static_cast<size_t>(capacity));
  auto *model = new JniModel(modelData);
  return reinterpret_cast<jlong>(model);
}

JNIEXPORT jstring JNICALL Java_com_neuralize_edgerunner_Model_nativeGetName(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  return env->NewStringUTF(model->getName().c_str());
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeGetNumInputs(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  return static_cast<jint>(model->getNumInputs());
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeGetNumOutputs(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  return static_cast<jint>(model->getNumOutputs());
}

JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Model_nativeGetInput(
    JNIEnv *env, jobject obj, jlong modelPtr, jint index) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->getInput(env, static_cast<size_t>(index));
}

JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Model_nativeGetOutput(
    JNIEnv *env, jobject obj, jlong modelPtr, jint index) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->getOutput(env, static_cast<size_t>(index));
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeGetDelegate(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->getDelegate();
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeApplyDelegate(
    JNIEnv *env, jobject obj, jlong modelPtr, jint delegate) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->applyDelegate(static_cast<int>(delegate));
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Model_nativeExecute(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  return model->execute();
}

JNIEXPORT void JNICALL Java_com_neuralize_edgerunner_Model_nativeDestroy(
    JNIEnv *env, jobject obj, jlong modelPtr) {
  auto *model = reinterpret_cast<JniModel *>(modelPtr);
  delete model;
}

JNIEXPORT void JNICALL Java_com_neuralize_edgerunner_Model_nativeSetLibDir(
        JNIEnv *env, jobject obj, jstring dir) {
    const char* dirCStr = env->GetStringUTFChars(dir, nullptr);
    std::string libDir{dirCStr};
    libDir += ";/vendor/lib/rfsa/adsp;/vendor/dsp/cdsp;/system/lib/rfsa/adsp;/system/vendor/lib/rfsa/adsp;/dsp";

    setenv("ADSP_LIBRARY_PATH", libDir.c_str(), 1 /*override*/);
}

}
