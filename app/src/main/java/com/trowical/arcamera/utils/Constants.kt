package com.trowical.arcamera.utils

import android.Manifest
import android.os.Build

//to share media
internal const val MEDIA_KEY = "MediaKey"

//DeepAR keys
internal const val NO_EFFECT_KEY = "none"
internal const val MASK_KEY = "mask"
internal const val EFFECT_KEY = "effect"
internal const val FILTER_KEY = "filter"

//VIDEO BUFFER SIZE
internal const val BUFFER_SIZE = 4096

val mainPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
    )
} else {
    arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
}
