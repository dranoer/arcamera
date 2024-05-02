package com.trowical.arcamera.utils

import android.app.Activity
import android.content.Intent
import com.trowical.arcamera.model.MediaItemObj

/**
 * share photo-video
 */
fun Activity.shareMedia(mediaItem: MediaItemObj) {
    mediaItem.uri?.let { uri ->
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = mediaItem.mimeType
        startActivity(Intent.createChooser(intent, "Share"))
    }
}