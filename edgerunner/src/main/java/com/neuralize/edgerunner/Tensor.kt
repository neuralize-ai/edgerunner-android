package com.neuralize.edgerunner

import java.nio.ByteBuffer
import java.nio.ByteOrder

enum class TensorType {
    UNSUPPORTED,
    NOTYPE,
    FLOAT32,
    FLOAT16,
    INT32,
    UINT32,
    INT8,
    UINT8,
}

class Tensor(private val nativeHandle: Long) {
    init {
        System.loadLibrary("tensor_jni")
    }

    fun getName(): String {
        return nativeGetName(nativeHandle)
    }

    fun getType(): TensorType {
        val typeValue = nativeGetType(nativeHandle)
        return TensorType.values().first { it.ordinal == typeValue }
    }

    fun getDimensions(): LongArray {
        return nativeGetDimensions(nativeHandle)
    }

    fun getSize(): Long {
        return nativeGetSize(nativeHandle)
    }

    fun getBuffer(): ByteBuffer {
        val buffer = nativeGetBuffer(nativeHandle)
        buffer.order(ByteOrder.nativeOrder())
        return buffer
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
