package com.teamecoscan.ecoscanrebirth.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.util.Size
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

data class Detection(val boundingBox: RectF, val label: String, val confidence: Float)

// Función nativa para cargar el modelo
private fun loadModelFile(context: Context, modelName: String): ByteBuffer {
    val assetFileDescriptor = context.assets.openFd(modelName)
    val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
    val fileChannel = fileInputStream.channel
    val startOffset = assetFileDescriptor.startOffset
    val declaredLength = assetFileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

@Composable
fun DetectionScreen(onBack: () -> Unit) {
    var detections by remember { mutableStateOf(emptyList<Detection>()) }
    var imageSize by remember { mutableStateOf(Size(480, 640)) }

    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    val topDetection = detections.maxByOrNull { it.confidence }
    val infoResiduo = topDetection?.let { detection ->
        val info = ClasificadorEcoScan.obtenerInfoContenedor(detection.label)
        InfoResiduo(
            nombreTraducido = info.nombreTraducido,
            contenedor = info.contenedor,
            consejoExtra = info.consejoExtra,
            confidence = detection.confidence
        )
    } ?: InfoResiduo(nombreTraducido = "", contenedor = TipoContenedor.DESCONOCIDO, consejoExtra = "", confidence = 0f)


    // Efecto de vibración cuando la IA está muy segura de haber encontrado basura
    LaunchedEffect(detections) {
        if (detections.any { it.confidence > 0.70f }) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraAnalysisView(onDetections = { newDetections, size ->
            detections = newDetections
            imageSize = size
        })
        DetectionOverlay(detections = detections, imageSize = imageSize)
        Column(modifier = Modifier.fillMaxSize()) {
            CustomTopBar(onBack = onBack)
            Box(modifier = Modifier.weight(1f)) // Espacio flexible para empujar la tarjeta hacia abajo
            TarjetaInstruccion(info = infoResiduo)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun CameraAnalysisView(
    onDetections: (detections: List<Detection>, imageSize: Size) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val interpreter = remember {
        try {
            val modelBuffer = loadModelFile(context, "best_float16.tflite")
            val options = Interpreter.Options().apply {
                setNumThreads(4) // Usar múltiples núcleos de tu dispositivo
            }
            Interpreter(modelBuffer, options)
        } catch (e: Exception) {
            Log.e("DetectionScreen", "Error loading TFLite model", e)
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose { interpreter?.close() }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    try {
                        val interpreterInstance = interpreter ?: return@setAnalyzer
                        val bitmap = imageProxy.toBitmap()
                        val imageSize = Size(bitmap.width, bitmap.height)

                        // 1. Resize y Normalización
                        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
                        val inputBuffer = ByteBuffer.allocateDirect(4 * 640 * 640 * 3)
                        inputBuffer.order(ByteOrder.nativeOrder())

                        val intValues = IntArray(640 * 640)
                        resizedBitmap.getPixels(intValues, 0, 640, 0, 0, 640, 640)

                        for (pixelValue in intValues) {
                            inputBuffer.putFloat(((pixelValue shr 16) and 0xFF) / 255.0f)
                            inputBuffer.putFloat(((pixelValue shr 8) and 0xFF) / 255.0f)
                            inputBuffer.putFloat((pixelValue and 0xFF) / 255.0f)
                        }

                        // 2. Cálculo dinámico de la memoria
                        val outputShape = interpreterInstance.getOutputTensor(0).shape() // [1, numElements, 8400]
                        val numElements = outputShape[1]
                        val outputBuffer = ByteBuffer.allocateDirect(4 * numElements * 8400)
                        outputBuffer.order(ByteOrder.nativeOrder())

                        // 3. Inferencia
                        interpreterInstance.run(inputBuffer, outputBuffer.rewind())

                        // 4. Leer resultados
                        val floatArray = FloatArray(numElements * 8400)
                        outputBuffer.rewind()
                        outputBuffer.asFloatBuffer().get(floatArray)

                        val detections = postProcessYoloV8(floatArray, numElements)
                        onDetections(detections, imageSize)

                    } catch(e: Exception) {
                        Log.e("DetectionScreen", "Error during analysis", e)
                    } finally {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
                } catch (e: Exception) {
                    Log.e("DetectionScreen", "CameraX binding failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun DetectionOverlay(detections: List<Detection>, imageSize: Size) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val scaleX = canvasWidth / imageSize.height.toFloat()
        val scaleY = canvasHeight / imageSize.width.toFloat()

        detections.forEach { detection ->
            val boundingBox = detection.boundingBox
            val rect = RectF(
                boundingBox.left * scaleX,
                boundingBox.top * scaleY,
                boundingBox.right * scaleX,
                boundingBox.bottom * scaleY
            )

            // Dibujar la caja con estilo Premium
            drawRect(
                color = Color(0xFF8BC34A), // Verde característico
                topLeft = Offset(rect.left, rect.top),
                size = ComposeSize(rect.width(), rect.height()),
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

// OJO AQUÍ: Las 6 clases maestras nuevas
private fun postProcessYoloV8(floatArray: FloatArray, numElements: Int): List<Detection> {

    // Diccionario actualizado con el dataset optimizado
    val labels = listOf("Biodegradable", "cardboard", "glass", "metal", "paper", "plastic")

    val confThreshold = 0.40f // Certeza mínima para mostrarlo (40%)
    val iouThreshold = 0.50f
    val numDetections = 8400 // Rejilla estándar de YOLOv8
    val detections = mutableListOf<Detection>()

    // Transponer la matriz matemática
    val transposedOutput = Array(numDetections) { FloatArray(numElements) }
    for (i in 0 until numElements) {
        for (j in 0 until numDetections) {
            transposedOutput[j][i] = floatArray[i * numDetections + j]
        }
    }

    for (detectionArray in transposedOutput) {
        val scores = detectionArray.sliceArray(4 until detectionArray.size)
        var maxScore = 0f
        var classId = -1
        scores.forEachIndexed { index, score ->
            if (score > maxScore) {
                maxScore = score
                classId = index
            }
        }

        if (maxScore > confThreshold) {
            val centerX = detectionArray[0]
            val centerY = detectionArray[1]
            val width = detectionArray[2]
            val height = detectionArray[3]
            val left = centerX - width / 2
            val top = centerY - height / 2
            val right = centerX + width / 2
            val bottom = centerY + height / 2

            val rect = RectF(left / 640f, top / 640f, right / 640f, bottom / 640f)

            // Asignar el nombre
            val labelName = if (classId != -1 && classId < labels.size) labels[classId] else "Objeto_Desc_$classId"
            detections.add(Detection(rect, labelName, maxScore))
        }
    }
    return applyNMS(detections, iouThreshold)
}

private fun applyNMS(detections: List<Detection>, iouThreshold: Float): List<Detection> {
    val sortedDetections = detections.sortedByDescending { it.confidence }
    val selectedDetections = mutableListOf<Detection>()
    val occupied = BooleanArray(sortedDetections.size)

    for (i in sortedDetections.indices) {
        if (!occupied[i]) {
            selectedDetections.add(sortedDetections[i])
            occupied[i] = true

            for (j in (i + 1) until sortedDetections.size) {
                if (!occupied[j]) {
                    val iou = calculateIOU(sortedDetections[i].boundingBox, sortedDetections[j].boundingBox)
                    if (iou > iouThreshold) {
                        occupied[j] = true
                    }
                }
            }
        }
    }
    return selectedDetections
}

private fun calculateIOU(rect1: RectF, rect2: RectF): Float {
    val x1 = max(rect1.left, rect2.left)
    val y1 = max(rect1.top, rect2.top)
    val x2 = min(rect1.right, rect2.right)
    val y2 = min(rect1.bottom, rect2.bottom)

    val intersectionArea = max(0f, x2 - x1) * max(0f, y2 - y1)
    val area1 = rect1.width() * rect1.height()
    val area2 = rect2.width() * rect2.height()
    val unionArea = area1 + area2 - intersectionArea

    return if (unionArea > 0) intersectionArea / unionArea else 0f
}

@Composable
private fun CustomTopBar(onBack: () -> Unit) {
    val view = LocalView.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    onBack()
                },
                modifier = Modifier.align(Alignment.CenterStart).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.primary)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "IA Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
