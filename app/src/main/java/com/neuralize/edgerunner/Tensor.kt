package com.neuralize.edgerunner

import java.nio.ByteBuffer

class Tensor(private val nativeHandle: Long) {
    init {
        System.loadLibrary("tensor_jni")
    }

    fun getName(): String {
        return nativeGetName(nativeHandle)
    }

    fun getType(): Int {
        return nativeGetType(nativeHandle)
    }

    fun getDimensions(): LongArray {
        return nativeGetDimensions(nativeHandle)
    }

    fun getSize(): Long {
        return nativeGetSize(nativeHandle)
    }

    fun getBuffer(): ByteBuffer {
        return nativeGetBuffer(nativeHandle)
    }

    protected fun finalize() {
        nativeDestroy(nativeHandle)
    }

    private external fun nativeGetName(nativeHandle: Long): String

    private external fun nativeGetType(nativeHandle: Long): Int

    private external fun nativeGetDimensions(nativeHandle: Long): LongArray

    private external fun nativeGetSize(nativeHandle: Long): Long

    private external fun nativeGetBuffer(nativeHandle: Long): ByteBuffer

    private external fun nativeDestroy(nativeHandle: Long)
}

