package com.neuralize.imageclassifier

import com.neuralize.edgerunner.Model

class ImageClassifier(private val modelPath: String) {
    private var model = Model(modelPath)

    fun classify(): String {
        var result = "no result"

        return result
    }
}