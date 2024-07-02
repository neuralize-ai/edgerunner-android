package com.neuralize.imageclassifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.neuralize.imageclassifier.ui.theme.EdgerunnerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EdgerunnerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Classifier(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Classifier(modifier: Modifier = Modifier) {
    val modelPath = "./mobilenet_v3_small"
    val imageClassifier = ImageClassifier()

    var result by remember {
        mutableStateOf("no result")
    }

    Text(
        text = "Result: $result!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EdgerunnerTheme {
        Greeting("Android")
    }
}