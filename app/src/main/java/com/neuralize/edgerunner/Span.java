package com.neuralize.edgerunner;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

@Platform(include = "nonstd/span.hpp")
@Namespace("nonstd")
public class Span<T> extends Pointer {
    static { Loader.load(); }
    public Span() { allocate(); }
    private native void allocate();
    public Span(Pointer p) { super(p); }
    public native @Cast("size_t") long size();
    public native @Index @ByRef T get(@Cast("size_t") long i);
    public native @Index void put(@Cast("size_t") long i, T value);
}

