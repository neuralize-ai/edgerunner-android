package com.neuralize.edgerunner;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

@Platform(include = {"edgerunner/model.hpp", "edgerunner/tensor.hpp"})
@Namespace("edge")
public class Model extends Pointer {
    static { Loader.load(); }
    public Model() { allocate(); }
    private native void allocate();
    public Model(@StdString String modelPath) { allocate(modelPath); }
    private native void allocate(@StdString String modelPath);
    public Model(Pointer p) { super(p); }
    public native void loadModel(@StdString String modelPath);
    public native @Cast("size_t") long getNumInputs();
    public native @Cast("size_t") long getNumOutputs();
    public native @ByRef Tensor getInput(@Cast("size_t") long index);
    public native @ByRef Tensor getOutput(@Cast("size_t") long index);
    public native @Cast("edge::DELEGATE") int getDelegate();
    public native @Cast("edge::STATUS") int applyDelegate(@Cast("edge::DELEGATE") int delegate);
    public native @Cast("edge::STATUS") int execute();
    public native @StdString String name();
    // Enum for DELEGATE
    public static class DELEGATE {
        public static final int CPU = 0;
        public static final int GPU = 1;
        public static final int NPU = 2;
    }
    // Enum for STATUS
    public static class STATUS {
        public static final int SUCCESS = 0;
        public static final int FAIL = 1;
    }
}
