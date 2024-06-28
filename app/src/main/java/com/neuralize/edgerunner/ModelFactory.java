package com.neuralize.edgerunner;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

@Platform(include = {"edgerunner/model.hpp", "edgerunner/edgerunner.h"})
@Namespace("edge")
public class ModelFactory {
    static { Loader.load(); }
    // Function to create a Model from a given file path
    public static native @ByVal Model createModel(@StdString String modelPath);
}