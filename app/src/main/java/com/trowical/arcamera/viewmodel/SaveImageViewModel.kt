package com.trowical.arcamera.viewmodel

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trowical.arcamera.utils.FileUtil
import com.trowical.arcamera.utils.message
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

class SaveImageViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var contentValues: ContentValues? = null
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    /**
     * save bitmap
     */
    fun saveBitmap(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            performSaveBitmap(bitmap)
        }
    }

    /**
     * insert image
     */
    private fun performSaveBitmap(bitmap: Bitmap) {
        try {
            val contentResolver = getApplication<Application>().contentResolver

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues = FileUtil.getPicturesContentValues(bitmap.width, bitmap.height)

                val uri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
                )
                uri?.let {
                    imageUri = it
                    contentResolver.openOutputStream(it, "w")
                        ?.let { openOutputStream ->
                            saveImageToStream(bitmap, openOutputStream)
                        }
                }

            } else {
                val file = File(
                    FileUtil.getPicturesFile(),
                    FileUtil.getFileName(".jpg")
                )
                imagePath = file.absolutePath
                saveImageToStream(bitmap, FileOutputStream(file))

            }
        } catch (e: FileNotFoundException) {
            e.message?.let { getApplication<Application>().message(it) }
        }
    }

    /**
     * copy image
     */
    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream) {
        val saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        if (saved) {
            notifyFile()
        }
    }

    /**
     * notify file to gallery
     */
    private fun notifyFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageUri?.let {
                contentValues?.clear()
                contentValues?.put(MediaStore.Images.Media.IS_PENDING, false)
                getApplication<Application>().contentResolver.update(
                    it, contentValues, null, null
                )
            }
        } else {
            MediaScannerConnection.scanFile(
                getApplication(), arrayOf(imagePath), null
            ) { path, uri ->
                Log.e("XXX", "Scanned $path:")
                Log.e("XXX", "-> uri=$uri")
            }
        }
    }

}