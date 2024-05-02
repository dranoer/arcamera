package com.trowical.arcamera.ui.camera

import ai.deepar.ar.ARErrorType
import ai.deepar.ar.AREventListener
import ai.deepar.ar.DeepAR
import ai.deepar.ar.DeepARImageFormat
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.Size
import android.view.Surface
import androidx.camera.core.ImageProxy
import com.trowical.arcamera.R
import com.trowical.arcamera.model.DeepARResult
import com.trowical.arcamera.utils.EFFECT_KEY
import com.trowical.arcamera.utils.FILTER_KEY
import com.trowical.arcamera.utils.MASK_KEY
import com.trowical.arcamera.utils.NO_EFFECT_KEY
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DeepARHelper(
    context: Context,
    val deepARResult: (result: DeepARResult) -> Unit
) : AREventListener {

    private var deepAR: DeepAR = DeepAR(context)

    var currentMask: String = NO_EFFECT_KEY
    var currentEffect: String = NO_EFFECT_KEY
    var currentFilter: String = NO_EFFECT_KEY

    private var buffers: Array<ByteBuffer?>? = null
    private var currentBuffer = 0
    private val numberOfBuffers = 2

    init {
        deepAR.setLicenseKey(context.getString(R.string.deep_ar_key))
        deepAR.initialize(context, this)
        deepAR.setAREventListener(this)
    }

    /**
     * apply new effect
     */
    fun applyEffect(slot: String, path: String) {
        deepAR.switchEffect(slot, path)
    }

    /**
     * take photo
     */
    fun takePhoto() {
        deepAR.takeScreenshot()
    }

    /**
     * start recording
     */
    fun startRecording(path: String) {
        deepAR.startVideoRecording(path, 720, 1280)
    }

    /**
     * stop recording
     */
    fun stopRecording() {
        deepAR.stopVideoRecording()
    }

    /**
     * set render surface
     */
    fun setRenderSurface(
        surface: Surface?, size: Size
    ) = deepAR.setRenderSurface(surface, size.width, size.height)

    /**
     * release deepAR
     */
    fun stopDeepAR() {
        deepAR.release()
    }

    /**
     * process image
     */
    fun processImage(image: ImageProxy, mirroring: Boolean) {
        buffers = arrayOfNulls(numberOfBuffers)
        for (i in 0 until numberOfBuffers) {
            buffers!![i] = ByteBuffer.allocateDirect(image.width * image.height * 3)
            buffers!![i]?.order(ByteOrder.nativeOrder())
            buffers!![i]?.position(0)
        }

        val byteData: ByteArray
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        byteData = ByteArray(ySize + uSize + vSize)

        yBuffer[byteData, 0, ySize]
        vBuffer[byteData, ySize, vSize]
        uBuffer[byteData, ySize + vSize, uSize]
        buffers!![currentBuffer]?.put(byteData)
        buffers!![currentBuffer]?.position(0)
        deepAR.receiveFrame(
            buffers!![currentBuffer],
            image.width, image.height,
            image.imageInfo.rotationDegrees,
            mirroring,
            DeepARImageFormat.YUV_420_888,
            image.planes[1].pixelStride
        )
        currentBuffer = (currentBuffer + 1) % numberOfBuffers
    }

    /**
     * notify photo
     */
    override fun screenshotTaken(bitmap: Bitmap) {
        deepARResult(DeepARResult.Photo(bitmap))
    }

    /**
     * notify video start recording
     */
    override fun videoRecordingStarted() {
        deepARResult(DeepARResult.VideoRecStarted)
    }

    /**
     * notify video stop recording
     */
    override fun videoRecordingFinished() {
        deepARResult(DeepARResult.VideoRecFinished)
    }

    /**
     * notify video failed recording
     */
    override fun videoRecordingFailed() {
        deepARResult(DeepARResult.VideoRecFailed)
    }

    override fun videoRecordingPrepared() {}

    override fun shutdownFinished() {}

    override fun initialized() {
        deepAR.switchEffect(MASK_KEY, currentMask)
        deepAR.switchEffect(EFFECT_KEY, currentEffect)
        deepAR.switchEffect(FILTER_KEY, currentFilter)
    }

    /**
     * apply old effects if it's necessary
     */
    fun applyOldEffects() {
        if (currentMask != NO_EFFECT_KEY) {
            deepAR.switchEffect(MASK_KEY, currentMask)
        }
        if (currentEffect != NO_EFFECT_KEY) {
            deepAR.switchEffect(EFFECT_KEY, currentEffect)
        }
        if (currentFilter != NO_EFFECT_KEY) {
            deepAR.switchEffect(FILTER_KEY, currentFilter)
        }
    }

    override fun faceVisibilityChanged(p0: Boolean) {}

    override fun imageVisibilityChanged(p0: String?, p1: Boolean) {}

    override fun frameAvailable(p0: Image?) {}

    /**
     * notify error
     */
    override fun error(errorType: ARErrorType, message: String) {
        if (errorType == ARErrorType.ERROR) {
            deepARResult(DeepARResult.Error(message))
        }
    }

    override fun effectSwitched(p0: String?) {}

}