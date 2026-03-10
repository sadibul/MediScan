package com.mediscan.app.ui.screens.patient.scan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.view.MotionEvent
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

/**
 * CameraPreviewScreen — full-screen CameraX preview with:
 * - MAXIMIZE_QUALITY capture for sharp prescription images
 * - Tap-to-focus for precise focus control
 * - Flash toggle for low-light conditions
 * - Higher JPEG quality (95%) for better AI extraction
 */
@Composable
fun CameraPreviewScreen(
    onImageCaptured: (ByteArray) -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera reference for focus/flash control
    var camera by remember { mutableStateOf<Camera?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }

    // Focus indicator state
    var showFocusRing by remember { mutableStateOf(false) }
    var focusX by remember { mutableStateOf(0f) }
    var focusY by remember { mutableStateOf(0f) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setJpegQuality(95)
            .build()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // CameraX Preview with tap-to-focus
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView ->
                    camera = startCamera(ctx, lifecycleOwner, previewView, imageCapture)

                    // Tap-to-focus listener
                    previewView.setOnTouchListener { view, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            val cam = camera
                            if (cam != null) {
                                val factory = previewView.meteringPointFactory
                                val point = factory.createPoint(event.x, event.y)
                                val action = FocusMeteringAction.Builder(point)
                                    .setAutoCancelDuration(3, TimeUnit.SECONDS)
                                    .build()
                                cam.cameraControl.startFocusAndMetering(action)

                                // Show focus ring indicator
                                focusX = event.x
                                focusY = event.y
                                showFocusRing = true

                                // Hide focus ring after a delay
                                view.postDelayed({ showFocusRing = false }, 1000)
                            }
                            true
                        } else {
                            false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Focus ring indicator
        if (showFocusRing) {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (focusX - with(density) { 30.dp.toPx() }).toInt(),
                            (focusY - with(density) { 30.dp.toPx() }).toInt()
                        )
                    }
                    .size(60.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(8.dp))
            )
        }

        // Top bar with close + flash buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Camera",
                    tint = Color.White
                )
            }

            // Flash toggle
            IconButton(
                onClick = {
                    isFlashOn = !isFlashOn
                    camera?.cameraControl?.enableTorch(isFlashOn)
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = if (isFlashOn) "Flash On" else "Flash Off",
                    tint = if (isFlashOn) Color.Yellow else Color.White
                )
            }
        }

        // Bottom guide text + capture button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hold steady • Tap to focus • Good lighting helps",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Capture button
            FloatingActionButton(
                onClick = {
                    // Set flash mode for capture if torch is on
                    imageCapture.flashMode = if (isFlashOn) {
                        ImageCapture.FLASH_MODE_ON
                    } else {
                        ImageCapture.FLASH_MODE_OFF
                    }

                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bytes = imageProxyToByteArray(image)
                                Log.d("CameraPreview", "Captured image: ${image.width}x${image.height}, rotation=${image.imageInfo.rotationDegrees}, bytes=${bytes.size}")
                                image.close()
                                onImageCaptured(bytes)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraPreview", "Capture failed", exception)
                            }
                        }
                    )
                },
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                containerColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

/**
 * Start CameraX — binds preview + imageCapture to the lifecycle.
 * Returns the Camera instance for focus/flash control.
 */
private fun startCamera(
    context: android.content.Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    imageCapture: ImageCapture,
): Camera? {
    var camera: Camera? = null
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener(
        {
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Camera bind failed", e)
            }
        },
        ContextCompat.getMainExecutor(context)
    )
    return camera
}

/**
 * Convert ImageProxy (from CameraX capture) to JPEG byte array.
 * Handles rotation correction. Uses high quality (95%) for AI extraction.
 */
private fun imageProxyToByteArray(image: ImageProxy): ByteArray {
    val buffer: ByteBuffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    // Decode and re-encode with rotation correction
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val rotatedBitmap = if (image.imageInfo.rotationDegrees != 0) {
        val matrix = Matrix()
        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }

    val outputStream = ByteArrayOutputStream()
    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
    return outputStream.toByteArray()
}
