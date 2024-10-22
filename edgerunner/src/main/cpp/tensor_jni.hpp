#include <edgerunner/tensor.hpp>
#include <jni.h>
#include <vector>

class JniTensor {
public:
  explicit JniTensor(std::shared_ptr<edge::Tensor> tensor)
      : m_tensor(std::move(tensor)) {}

  std::string getName() const { return m_tensor->getName(); }

  int getType() const { return static_cast<int>(m_tensor->getType()); }

  std::vector<size_t> getDimensions() const {
    return m_tensor->getDimensions();
  }

  size_t getSize() const { return m_tensor->getSize(); }

  template <typename T> nonstd::span<T> getTensorAs() {
    return m_tensor->getTensorAs<T>();
  }

private:
  std::shared_ptr<edge::Tensor> m_tensor;
};
