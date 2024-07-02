package com.neuralize.imageclassifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neuralize.imageclassifier.ui.theme.EdgerunnerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.ByteBuffer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EdgerunnerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Classifier(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Classifier(modifier: Modifier = Modifier) {
    var results by remember {
        mutableStateOf(Results())
    }

    var imageFile by remember {
        mutableStateOf("keyboard.jpg")
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        ImageFilePicker(
            currentImageFile = imageFile,
            onFileChoice = {
                imageFile = it
            },
        )

        Classify(
            imageFileName = imageFile,
            onResult = {
                results = it
            },
        )

        HorizontalDivider()

        ResultsDisplay(results)
    }
}

@Composable
fun ImageFilePicker(
    currentImageFile: String,
    onFileChoice: (String) -> Unit,
) {
    val context = LocalContext.current
    val imageFiles: List<String> =
        context.assets.list("")?.filter { it.endsWith(".jpg") || it.endsWith(".png") }
            ?: emptyList()
    var showFilePicker by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { showFilePicker = true }) {
            Text("Choose image")
        }
        Text("Current file: $currentImageFile")
    }

    if (showFilePicker) {
        AlertDialog(
            onDismissRequest = { showFilePicker = false },
            title = {
                Text("Choose image")
            },
            text = {
                LazyColumn {
                    items(imageFiles) { fileName ->
                        Text(
                            text = fileName,
                            color = if (currentImageFile == fileName) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.fillMaxWidth().padding(0.dp, 8.dp).clickable {
                                    onFileChoice(fileName)
                                },
                        )
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showFilePicker = false }) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
fun Classify(
    imageFileName: String,
    onResult: (Results) -> Unit,
) {
    val context = LocalContext.current

    val modelFileName = "mobilenet_v3_small.tflite"

    var modelBuffer: ByteBuffer?

    var imageClassifier: ImageClassifier? by remember { mutableStateOf(null) }

    var isExecuting by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val modelStream: InputStream = context.assets.open(modelFileName)
            val fileSize = modelStream.available()
            modelBuffer = ByteBuffer.allocateDirect(fileSize)

            modelBuffer?.let { modelBuffer ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (modelStream.read(buffer).also { bytesRead = it } != -1) {
                    modelBuffer.put(buffer, 0, bytesRead)
                }
                modelStream.close()
                modelBuffer.flip() // Prepare the buffer for reading

                imageClassifier = ImageClassifier(context, modelBuffer)
            }
        }
    }

    var executeJob: Job? by remember { mutableStateOf(null) }

    val executionScope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {

        Button(
            onClick = {
                executeJob = executionScope.launch {
                    isExecuting = true
                    withContext(Dispatchers.IO) {
                        val results = imageClassifier?.classify(imageFileName)
                        results?.let { onResult(it) }
                        isExecuting = false
                    }
                }
            },
            enabled = !isExecuting && imageClassifier != null,
        ) {
            Text("Classify")
        }

        if (isExecuting || (imageClassifier == null)) {
            CircularProgressIndicator()
        }

        Button(
            onClick = {
                executeJob?.cancel()
                isExecuting = false
            },
            enabled = isExecuting,
        ) {
            Text("Cancel")
        }
    }
}

@Composable
fun ResultsDisplay(results: Results) {
    Card {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Prediction: ${results.prediction} (${100 * results.probability}%)")
            Text("LoadTime (ms): ${results.loadTime}")
            Text("Inference time (ms): ${results.inferenceTime}")
            Text("total time (ms): ${results.totalTime}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClassifierPreview() {
    EdgerunnerTheme {
        Classifier()
    }
}
