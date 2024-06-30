#include "tensor_jni.hpp"

extern "C" {

JNIEXPORT jstring JNICALL Java_com_neuralize_edgerunner_Tensor_nativeGetName(
    JNIEnv *env, jobject obj, jlong nativeHandle) {
  auto jniTensor = reinterpret_cast<JniTensor *>(nativeHandle);
  return env->NewStringUTF(jniTensor->getName().c_str());
}

JNIEXPORT jint JNICALL Java_com_neuralize_edgerunner_Tensor_nativeGetType(
    JNIEnv *env, jobject obj, jlong nativeHandle) {
  auto jniTensor = reinterpret_cast<JniTensor *>(nativeHandle);
  return jniTensor->getType();
}

JNIEXPORT jlongArray JNICALL
Java_com_neuralize_edgerunner_Tensor_nativeGetDimensions(JNIEnv *env,
                                                         jobject obj,
                                                         jlong nativeHandle) {
  auto jniTensor = reinterpret_cast<JniTensor *>(nativeHandle);
  auto dimensions = jniTensor->getDimensions();
  jlongArray result = env->NewLongArray(dimensions.size());
  env->SetLongArrayRegion(result, 0, dimensions.size(),
                          reinterpret_cast<const jlong *>(dimensions.data()));
  return result;
}

JNIEXPORT jlong JNICALL Java_com_neuralize_edgerunner_Tensor_nativeGetSize(
    JNIEnv *env, jobject obj, jlong nativeHandle) {
  auto jniTensor = reinterpret_cast<JniTensor *>(nativeHandle);
  return static_cast<jlong>(jniTensor->getSize());
}

JNIEXPORT jobject JNICALL Java_com_neuralize_edgerunner_Tensor_nativeGetBuffer(
    JNIEnv *env, jobject obj, jlong nativeHandle) {
  auto jniTensor = reinterpret_cast<JniTensor *>(nativeHandle);
  auto span = jniTensor->getTensorAs<uint8_t>();
  return env->NewDirectByteBuffer(span.data(), span.size());
}

JNIEXPORT void JNICALL Java_com_neuralize_edgerunner_Tensor_nativeDestroy(
    JNIEnv *env, jobject obj, jlong nativeHandle) {
  auto jniTensor = reinterpret_cast<JniTensor *>(nativeHandle);
  delete jniTensor;
}
}
