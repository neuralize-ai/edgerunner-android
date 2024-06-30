#include "tensor_jni.hpp"
#include <edgerunner/edgerunner.hpp>
#include <edgerunner/model.hpp>
#include <jni.h>
#include <string>

class JniModel {
public:
  JniModel(const std::string &modelPath)
      : m_model(edge::createModel(modelPath)) {}

  size_t getNumInputs() const { return m_model->getNumInputs(); }

  size_t getNumOutputs() const { return m_model->getNumOutputs(); }

  jlong getInput(JNIEnv *env, size_t index) const {
    auto tensor = m_model->getInput(index);

    if (tensor) {
      JniTensor *tensorPtr = new JniTensor(tensor);
      return reinterpret_cast<jlong>(tensorPtr);
    }

    return 0;
  }

  jlong getOutput(JNIEnv *env, size_t index) const {
    auto tensor = m_model->getOutput(index);

    if (tensor) {
      JniTensor *tensorPtr = new JniTensor(tensor);
      return reinterpret_cast<jlong>(tensorPtr);
    }

    return 0;
  }

  jint getDelegate() const { return static_cast<jint>(m_model->getDelegate()); }

  jint applyDelegate(int delegate) {
    return static_cast<jint>(
        m_model->applyDelegate(static_cast<edge::DELEGATE>(delegate)));
  }

  jint execute() { return static_cast<jint>(m_model->execute()); }

  std::string getName() const { return m_model->name(); }

private:
  std::unique_ptr<edge::Model> m_model;
};
