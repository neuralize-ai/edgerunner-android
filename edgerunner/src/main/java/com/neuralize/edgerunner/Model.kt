package com.neuralize.edgerunner

import java.nio.ByteBuffer

enum class Delegate {
    CPU,
    GPU,
    NPU,
}

enum class Status {
    SUCCESS,
    FAIL,
}

class Model(nativeLibraryDir: String) {
    private var nativeHandle: Long = 0

    init {
        System.loadLibrary("model_jni")

        nativeSetLibDir(nativeLibraryDir)
    }

    constructor(nativeLibraryDir: String, modelPath: String) : this(nativeLibraryDir) {
        nativeHandle = nativeCreate(modelPath)
    }

    constructor(nativeLibraryDir: String, modelBuffer: ByteBuffer) : this(nativeLibraryDir) {
        nativeHandle = nativeCreateFromBuffer(modelBuffer)
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

    fun getDelegate(): Delegate {
        val delegate = nativeGetDelegate(nativeHandle)
        return Delegate.values().first { it.ordinal == delegate }
    }

    fun applyDelegate(delegate: Delegate): Int {
        return nativeApplyDelegate(nativeHandle, delegate.ordinal)
    }

    fun execute(): Status {
        val statusValue = nativeExecute(nativeHandle)
        return Status.values().first { it.ordinal == statusValue }
    }

    protected fun finalize() {
        nativeDestroy(nativeHandle)
    }

    private external fun nativeCreate(modelPath: String): Long

    private external fun nativeCreateFromBuffer(modelBuffer: ByteBuffer): Long

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

    private external fun nativeSetLibDir(dir: String)
}
