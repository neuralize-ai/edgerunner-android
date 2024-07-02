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

    private fun centerCropImage(
        image: Mat,
        cropHeight: Int,
        cropWidth: Int,
    ): Mat {
        var imageHeight = image.rows()
        var imageWidth = image.cols()

        if (cropHeight > imageWidth || cropWidth > imageHeight) {
            val padLeft = (cropHeight - imageWidth) / 2
            val padTop = (cropWidth - imageHeight) / 2
            val padRight = (cropHeight - imageWidth + 1) / 2
            val padBottom = (cropWidth - imageHeight + 1) / 2

            Core.copyMakeBorder(
                image,
                image,
                padTop,
                padBottom,
                padLeft,
                padRight,
                Core.BORDER_CONSTANT,
                Scalar(0.0, 0.0, 0.0),
            )
            imageHeight = image.rows()
            imageWidth = image.cols()
        }

        val cropTop = floor((imageHeight - cropWidth) / 2.0).toInt()
        val cropLeft = floor((imageWidth - cropHeight) / 2.0).toInt()

        val cropRegion = Rect(cropLeft, cropTop, cropHeight, cropWidth)
        return image.submat(cropRegion)
    }

    private fun normalizeImage(image: Mat) {
        val mean = Scalar(0.485, 0.456, 0.406)
        val std = Scalar(0.229, 0.224, 0.225)

        Core.subtract(image, mean, image)
        Core.divide(image, std, image)
    }

    private fun printPixel(buffer: ByteBuffer, width: Int, channels: Int, hIndex: Int, wIndex: Int) {
        val red = buffer.asFloatBuffer().get(hIndex * width * channels + wIndex * channels)
        val green = buffer.asFloatBuffer().get(hIndex * width * channels + wIndex * channels + 1)
        val blue = buffer.asFloatBuffer().get(hIndex * width * channels + wIndex * channels + 2)
        Log.d("ImageClassifier", "pixel (${hIndex}, ${wIndex}): [${red}, ${green}, ${blue}")
    }

    private fun printBuffer(buffer: ByteBuffer, message: String) {
        val bufferArray = FloatArray(buffer.asFloatBuffer().remaining())
        buffer.asFloatBuffer().get(bufferArray)
        Log.d("ImageClassifier", "${message}:\nsize: ${bufferArray.size}\nvalues: ${bufferArray.joinToString(", ")}")
    }

    private fun writeImageToInputBuffer(
        image: Mat,
        buffer: ByteBuffer,
    ) {
        val height = image.rows()
        val width = image.cols()

        val numChannels = image.channels()
        val rowSize = width * numChannels * 4 // 4 bytes per float

        for (i in 0 until height) {
            val hOffset = i * rowSize
            for (j in 0 until width) {
                val wOffset = hOffset + j * numChannels * 4
                val pixel = image.get(i, j)
                buffer.putFloat(wOffset, pixel[0].toFloat())
                buffer.putFloat(wOffset + 4, pixel[1].toFloat())
                buffer.putFloat(wOffset + 8, pixel[2].toFloat())
            }
        }
    }

    private fun softmax(output: ByteBuffer): List<Float> {
        val floatBuffer = output.asFloatBuffer()
        val outputList = FloatArray(floatBuffer.remaining())
        floatBuffer.get(outputList)

        val maxOutput = outputList.maxOrNull() ?: throw IllegalArgumentException("output buffer is empty")

        val softmaxValues = outputList.map { exp((it - maxOutput).toDouble()).toFloat() }

        val expSum = softmaxValues.sum()

        return softmaxValues.map { it / expSum }
    }
}
