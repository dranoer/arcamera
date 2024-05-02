package com.trowical.arcamera.utils

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.MediaStore

object CursorUtils {

    val CONTENT_URI: Uri = MediaStore.Files.getContentUri("external")

    fun getMediaContentUri(mediaType: Int): Uri =
        if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI else
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    const val SORT_BY_DATE = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

    @SuppressLint("InlinedApi")
    val PROJECTION = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DURATION,
        MediaStore.Files.FileColumns.DATE_ADDED
    )

    const val SELECTION = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = " +
            "${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE} " +
            "OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = " +
            "${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}"

}