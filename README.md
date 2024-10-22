<h1 align="center">
    <a href="https://runlocal.ai">
        <img src="./images/large-logo.png" width="300">
    </a>
    <br><br>
    Edgerunner Android
</h1>

<h4 align="center">
    Kotlin bindings for Edgerunner
</h4>

<div align="center">
    <a href="https://runlocal.ai">Website</a> |
    <a href="https://runlocal.ai#contact">Contact</a> |
    <a href="https://discord.gg/y9EzZEkwbR">Discord</a> |
    <a href="https://x.com/Neuralize_AI">Twitter</a>
</div>

## 💡 Introduction

Edgerunner Android provides a Kotlin wrapper for the [Edgerunner](https://github.com/neuralize-ai/edgerunner) AI inference library. This library is a work in progress, currently offering inference of tflite models on CPU. Support for GPU and vendor-specific NPU inference will follow incrementally along with various other inference engines. See [Edgerunner](https://github.com/neuralize-ai/edgerunner) for more details.

The wrapper logic and public Kotlin classes are found in the [edgerunner Android library](./edgerunner).

## 🛠 Installation

The library will soon be published to Maven Central.

## 🕹 Usage

An example [image classification app](./imageclassifier) is bundled with the project. See [imageclassifier.kt](./imageclassifier/src/main/java/com/neuralize/imageclassifier/ImageClassifier.kt) for edgerunner API usage.

In general, the library can be used as follows;

```kotlin
import com.neuralize.edgerunner.Model

// ...

/* read model file into a ByteBuffer -> modelBuffer */
// ...

val model = Model(modelBuffer.asReadOnlyBuffer())

/* ByteBuffer, direct access to input buffer for model inference */
val inputBuffer = model.getInput(0)?.getBuffer() ?: /* handle error */

/* write input to `inputBuffer` */
// ...

val executionStatus = model.execute()


val outputBuffer = model.getOutput(0)?.getBuffer() ?: /* handle error */

/* interpret output */
// ...
```

The full API for `Model` and `Tensor` can be found in [Model.kt](./edgerunner/src/main/java/com/neuralize/edgerunner/Model.kt) and [Tensor.kt](./edgerunner/src/main/java/com/neuralize/edgerunner/Tensor.kt) respectively.

## 📜 Licensing

See the [LICENSING](LICENSE.txt) document.
