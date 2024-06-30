package com.neuralize.edgerunner

class Model(private val modelPath: String) {
    private var nativeHandle: Long = 0

    init {
        System.loadLibrary("model_jni")
        nativeHandle = nativeCreate(modelPath)
    }

    fun getName(): String {
        return nativeGetName(nativeHandle)
    }

    fun getNumInputs(): Int {
        return nativeGetNumInputs(nativeHandle)
    }

    fun getNumOutputs(): Int {
        return nativeGetNumOutputs(nativeHandle)
    }

    fun getInput(index: Int): Tensor? {
        val tensorHandle = nativeGetInput(nativeHandle, index)
        return if (tensorHandle != 0L) Tensor(tensorHandle) else null
    }

    fun getOutput(index: Int): Tensor? {
        val tensorHandle = nativeGetOutput(nativeHandle, index)
        return if (tensorHandle != 0L) Tensor(tensorHandle) else null
    }

    fun getDelegate(): Int {
        return nativeGetDelegate(nativeHandle)
    }

    fun applyDelegate(delegate: Int): Int {
        return nativeApplyDelegate(nativeHandle, delegate)
    }

    fun execute(): Int {
        return nativeExecute(nativeHandle)
    }

    protected fun finalize() {
        nativeDestroy(nativeHandle)
    }

    private external fun nativeCreate(modelPath: String): Long

    private external fun nativeGetName(nativeHandle: Long): String

    private external fun nativeGetNumInputs(nativeHandle: Long): Int

    private external fun nativeGetNumOutputs(nativeHandle: Long): Int

    private external fun nativeGetInput(
        nativeHandle: Long,
        index: Int,
    ): Long

    private external fun nativeGetOutput(
        nativeHandle: Long,
        index: Int,
    ): Long

    private external fun nativeGetDelegate(nativeHandle: Long): Int

    private external fun nativeApplyDelegate(
        nativeHandle: Long,
        delegate: Int,
    ): Int

    private external fun nativeExecute(nativeHandle: Long): Int

    private external fun nativeDestroy(nativeHandle: Long)
}
