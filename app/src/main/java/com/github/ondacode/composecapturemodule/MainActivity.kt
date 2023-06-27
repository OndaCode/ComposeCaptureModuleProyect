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
                //Obtiene el contexto local actual
                val context = LocalContext.current
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Declara  2 variables mutables para recuperar el width y height de tu composable
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
//Encierra tu composable en una lamda
                        val composableContent: @Composable () -> Unit = {
                            //Modifica tu composable para que retorne el widht y height en una lamda
                            CatInfo { width: Int, heigth: Int ->
                                composableWidth.value = width
                                composableHeight.value = heigth
                            }
                        }
                        //Para actualizar el tamaño de tu composable tienes que invocarlo
                        composableContent.invoke()

                        //Valida que tu composable te retorne un width y un height
                        if (composableWidth.value > 0 && composableHeight.value > 0) {
                            //Crea una instancia de IntSize con el tamaño de tu composable
                            val intSize = IntSize(
                                width = composableWidth.value,
                                height = composableHeight.value
                            )
                            //Crea una instancia del modelo OCComposeCaptureModel
                            val ocComposeCaptureModel = OCComposeCaptureModel(
                                composableContent = composableContent,
                                intSize = intSize
                            ) {  //Aqui recuperas el bitmap de composable
                                    resposeBitmap ->

                                //Opcional guardas el bitmap como una imagen con extension png
                                val file = File(context.filesDir, "screenshot1.png")
                                FileOutputStream(file).use { outputStream ->
                                    resposeBitmap?.compress(
                                        Bitmap.CompressFormat.PNG,
                                        100,
                                        outputStream
                                    )
                                }
                            }
                            //LLamas el composable OCComposeCapture agregando los datos del modelo OCComposeCaptureModel
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
                //Obtenen el width y height de tu composable
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
                //Retorna los valores width y height de tu composable en una lamda
                size(widthInPixels, heightInPixels)
            }) {

        for (i in 0..300) {
            Text("Item number : $i", fontSize = 14.sp)
        }
    }
}