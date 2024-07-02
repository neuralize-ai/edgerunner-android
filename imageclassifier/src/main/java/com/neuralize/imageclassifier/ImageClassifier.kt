package com.neuralize.imageclassifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.neuralize.edgerunner.Model
import com.neuralize.edgerunner.Status
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.android.OpenCVLoader
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.math.exp
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.measureTimedValue

class ImageClassifier(private val context: Context, modelBuffer: ByteBuffer) {
    private var model: Model
    private val labelList: List<String>

    init {
        OpenCVLoader.initLocal()

        model = Model(modelBuffer.asReadOnlyBuffer())

        labelList = loadLabelList("imagenet_labels.txt")
    }

    fun classify(imageFilename: String): String {
        return result
    }
    private fun loadLabelList(labelsFileName: String): List<String> {
        val assetManager = context.assets
        val inputStream = assetManager.open(labelsFileName)
        val labels = inputStream.bufferedReader().use { it.readLines() }
        return labels
    }

    private fun loadImageFromAssets(
        context: Context,
        fileName: String,
    ): Bitmap? {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open(fileName)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB)
        mat.convertTo(mat, CvType.CV_32F, 1.0 / 255)
        return mat
    }
    private fun resizeImage(
        image: Mat,
        height: Int,
        width: Int,
    ) {
        val scaleDimSize = maxOf(height, width)

        // next power of two
        val scaledSize = 1 shl (32 - Integer.numberOfLeadingZeros(scaleDimSize - 1))

        val imageHeight = image.rows()
        val imageWidth = image.cols()

        val longDim = maxOf(imageHeight, imageWidth).toFloat()
        val shortDim = minOf(imageHeight, imageWidth).toFloat()

        val newLong = (scaledSize.toFloat() * longDim / shortDim).toInt()

        val newHeight = if (imageHeight > imageWidth) newLong else scaledSize
        val newWidth = if (imageHeight > imageWidth) scaledSize else newLong

        Imgproc.resize(image, image, Size(newWidth.toDouble(), newHeight.toDouble()), 0.0, 0.0, Imgproc.INTER_LINEAR)
    }

}
