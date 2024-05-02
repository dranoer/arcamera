package com.trowical.arcamera.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.util.MimeTypes
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {

    private const val FOLDER_PHOTO_EDIT = "FKPhotos"
    private const val FOLDER_VIDEO_EDIT = "FKVideos"

    /**
     * get files directory
     */
    fun getFilesDir(
        context: Context
    ) = File(context.filesDir, getFileName(".mp4"))

    /**
     *  file name
     */
    fun getFileName(extension: String): String {
        return SimpleDateFormat(
            "EEE_dd_MMM_yyyy_HH:mm:ss",
            Locale.getDefault()
        ).format(Calendar.getInstance().time) + extension
    }

    /***
     * pictures file
     */
    fun getPicturesFile(): File {
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).toString() + File.separator + FOLDER_PHOTO_EDIT
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * videos file
     */
    fun getVideosFile(): File {
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            ).toString() + File.separator + FOLDER_VIDEO_EDIT
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * content values to save image into folder
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getPicturesContentValues(width: Int, height: Int) = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, getFileName(".jpg"))
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Images.Media.MIME_TYPE, MimeTypes.IMAGE_JPEG)
        put(MediaStore.Images.Media.WIDTH, width)
        put(MediaStore.Images.Media.HEIGHT, height)
        put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + File.separator + FOLDER_PHOTO_EDIT
        )
        put(MediaStore.Images.Media.IS_PENDING, true)
    }

    /**
     * content values to save video into folder
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getVideoContentValues() = ContentValues().apply {
        val name = getFileName(".mp4")
        put(MediaStore.Video.Media.TITLE, name)
        put(MediaStore.Video.Media.DISPLAY_NAME, name)
        put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Video.Media.MIME_TYPE, MimeTypes.VIDEO_MP4)
        put(
            MediaStore.Video.Media.RELATIVE_PATH,
            Environment.DIRECTORY_MOVIES + File.separator + FOLDER_VIDEO_EDIT
        )
        put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
        put(MediaStore.Video.Media.IS_PENDING, true)
    }

}