package com.neuralize.edgerunner;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

@Platform(include = "edgerunner/tensor.hpp")
@Namespace("edge")
public class Tensor extends Pointer {
    static { Loader.load(); }
    public Tensor() { allocate(); }
    private native void allocate();
    public Tensor(Pointer p) { super(p); }
    public native @StdString String getName();
    public native @Cast("edge::TensorType") int getType();
    public native @StdVector SizeTPointer getDimensions();
    public native long getSize();
    @Name("getTensorAs")
    public native <T> @ByVal Span<T> getTensorAs(@Cast("void*") Pointer dataPtr, @Cast("size_t") long numBytes, @Cast("size_t") long numElementBytes);
    protected native @Cast("void*") Pointer getDataPtr();
    protected native long getNumBytes();
    // Enum for TensorType
    public static class TensorType {
        public static final int UNSUPPORTED = 0;
        public static final int NOTYPE = 1;
        public static final int FLOAT32 = 2;
        public static final int FLOAT16 = 3;
        public static final int INT32 = 4;
        public static final int UINT32 = 5;
        public static final int INT8 = 6;
        public static final int UINT8 = 7;
    }
}