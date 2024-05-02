package com.trowical.arcamera.model

import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class MediaItemObj(
    val id: Long = 0L,
    var uri: Uri? = Uri.EMPTY,
    val nameRaw: String? = "Unknown",
    val folderName: String? = "Unknown",
    val size: Long = 0L,
    val mediaType: Int = MediaStore.Files.FileColumns.MEDIA_TYPE_NONE,
    val mimeType: String = "",
    val duration: Long = 0L,
    val dateAdded: Long = 0L,
    //remove extension .mp4 .jpg
    val name: String? = nameRaw?.substringBeforeLast(".") ?: "Unknown"
) : Parcelable {

    /**
     * true if media is video
     */
    fun isVideo() = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO

    fun getDayMonthYear(): String = SimpleDateFormat(
        "dd MMMM yyy", Locale.getDefault()
    ).format(Date(dateAdded * 1000L))

}