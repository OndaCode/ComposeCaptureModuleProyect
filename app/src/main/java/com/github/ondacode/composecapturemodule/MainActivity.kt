package com.github.ondacode.composecapturemodule

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.github.ondacode.composecapture.OCComposeCapture
import com.github.ondacode.composecapture.OCComposeCaptureModel
import com.github.ondacode.composecapturemodule.ui.theme.ComposeCaptureModuleProyectTheme
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCaptureModuleProyectTheme {
                val context = LocalContext.current
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val composableWidth = remember {
                        mutableStateOf(0)
                    }
                    val composableHeight = remember {
                        mutableStateOf(0)
                    }
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        val composableContent: @Composable () -> Unit = {
                            CatInfo { width: Int, heigth: Int ->
                                composableWidth.value = width
                                composableHeight.value = heigth
                            }
                        }
                        composableContent.invoke()
                        if (composableWidth.value > 0 && composableHeight.value > 0) {
                            val intSize =
                                IntSize(width = composableWidth.value, height = composableHeight.value)
                            val ocComposeCaptureModel = OCComposeCaptureModel(
                                composableContent = composableContent,
                                intSize = intSize
                            ) { resposeBitmap ->
                                val file = File(context.filesDir, "screenshot1.png")
                                FileOutputStream(file).use { outputStream ->
                                    resposeBitmap?.compress(
                                        Bitmap.CompressFormat.PNG,
                                        100,
                                        outputStream
                                    )
                                }
                            }
                            OCComposeCapture(ocComposeCaptureModel = ocComposeCaptureModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeCaptureModuleProyectTheme {
        Greeting("Android")
    }
}
@Composable
fun CatInfo(size: (width: Int, height: Int) -> Unit) {
    val localDensity = LocalDensity.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .onGloballyPositioned { layoutCoordinates ->
                val heightInPixels =
                    with(localDensity) {
                        layoutCoordinates.size.height
                            .toDp()
                            .toPx()
                            .roundToInt()
                    }
                val widthInPixels =
                    with(localDensity) {
                        layoutCoordinates.size.width
                            .toDp()
                            .toPx()
                            .roundToInt()
                    }
                size(widthInPixels, heightInPixels)
            }) {

        for (i in 0..300) {
            Text("Item number : $i", fontSize = 14.sp)
        }
    }
}