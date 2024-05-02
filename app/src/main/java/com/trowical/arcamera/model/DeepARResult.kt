package com.trowical.arcamera.model

import android.graphics.Bitmap

sealed class DeepARResult {

    object VideoRecStarted : DeepARResult()

    object VideoRecFinished : DeepARResult()

    object VideoRecFailed : DeepARResult()

    data class Photo(
        val bitmap: Bitmap
    ) : DeepARResult()

    data class Error(
        val message: String
    ) : DeepARResult()

}