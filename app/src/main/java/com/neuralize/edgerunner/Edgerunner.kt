package com.neuralize.edgerunner

class Edgerunner {
    companion object {
        init {
            System.loadLibrary("edgerunner_jni")
        }
    }

    external fun createModel(modelPath: String): Long

    external fun applyDelegate(
        modelPtr: Long,
        delegate: Int,
    ): Int

    external fun execute(modelPtr: Long): Int

    external fun getModelName(modelPtr: Long): String

    external fun deleteModel(modelPtr: Long)
}

