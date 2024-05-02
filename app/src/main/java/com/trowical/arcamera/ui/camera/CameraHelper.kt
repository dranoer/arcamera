package com.trowical.arcamera.ui.camera

import ai.deepar.ar.CameraResolutionPreset
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.trowical.arcamera.model.DeepARResult
import com.google.common.util.concurrent.ListenableFuture
import com.trowical.arcamera.R.*
import com.trowical.arcamera.R.drawable.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraHelper(
    var context: Context,
    val deepARResult: (result: DeepARResult) -> Unit
) {

    val deepAR: DeepARHelper = DeepARHelper(context) { result -> deepARResult(result) }

    //private lateinit var lifecycle: CustomLifecycle
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageAnalysis: ImageAnalysis
    private val cameraPreset = CameraResolutionPreset.P640x480
    private var lens = CameraSelector.LENS_FACING_FRONT
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var camera: Camera? = null
    var videoFile: File? = null

    /**
     * start camera
     */
    fun startCamera() {
        //lifecycle = CustomLifecycle()
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCamera()
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * init image analysis and set analyzer
     */
    private fun initImageAnalysis() {
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(cameraPreset.height, cameraPreset.width))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor) { image ->
            deepAR.processImage(image, lens == CameraSelector.LENS_FACING_FRONT)
            image.close()
        }
    }

    /**
     * bind camera
     */
    private fun bindCamera() {
        try {
            initImageAnalysis()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lens)
                .build()

            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                imageAnalysis
            )
            deepAR.applyOldEffects()
        } catch (e: Exception) {
            e.message?.let { deepARResult(DeepARResult.Error(it)) }
        }
    }

    /**
     * enable-disable flash
     * notify icon
     */
    fun toggleFlash(notifyIcon: (icon: Int) -> Unit) {
        var icon = ic_flash_off
        val enable = when (flashMode) {
            ImageCapture.FLASH_MODE_OFF -> {
                flashMode = ImageCapture.FLASH_MODE_ON
                icon = ic_flash_on
                true
            }
            ImageCapture.FLASH_MODE_ON -> {
                flashMode = ImageCapture.FLASH_MODE_OFF
                icon = ic_flash_off
                false
            }
            else -> {
                false
            }
        }
        notifyIcon(icon)
        camera?.cameraControl?.enableTorch(enable)
    }

    /**
     * set zoom
     */
    fun setZoom(zoom: Float) {
        camera?.cameraControl?.setLinearZoom(zoom)
    }

    /**
     * switch camera
     */
    fun switchCamera() {
        lens = if (lens == CameraSelector.LENS_FACING_FRONT)
            CameraSelector.LENS_FACING_BACK else
            CameraSelector.LENS_FACING_FRONT
        bindCamera()
    }

    /**
     * stop camera
     */
    fun stopCamera() {
        imageAnalysis.clearAnalyzer()
        Handler(Looper.getMainLooper()).post {
            cameraProvider.unbindAll()
        }
        cameraProviderFuture.cancel(true)
        deepAR.stopDeepAR()
        //lifecycle.destroy()
    }

}

/**
 * custom lifecycle for camera
 */
/*
class CustomLifecycle : LifecycleOwner {
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    fun destroy() = Handler(Looper.getMainLooper()).post {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}*/