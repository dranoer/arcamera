package com.trowical.arcamera.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trowical.arcamera.utils.BUFFER_SIZE
import com.trowical.arcamera.utils.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*

class SaveVideoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var contentValues: ContentValues? = null
    private var videoRecordedPath: String? = null
    private var videoUri: Uri? = null
    private var videoPath: String? = null

    /**
     * save video
     */
    fun saveVideo(path: String) {
        videoRecordedPath = path
        viewModelScope.launch(Dispatchers.IO) {
            performSaveVideo()
        }
    }

    /**
     * insert video
     */
    private fun performSaveVideo() {
        val resolver: ContentResolver = getApplication<Application>().contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues = FileUtil.getVideoContentValues()
            val collection = MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
            resolver.insert(collection, contentValues)?.let { uriSavedVideo ->
                this.videoUri = uriSavedVideo
                copyVideoToGallery(uriSavedVideo)
            }
        } else {
            val videoFile = File(
                FileUtil.getVideosFile(),
                FileUtil.getFileName(".mp4")
            )
            videoPath = videoFile.absolutePath
            copyVideoToGallery(Uri.fromFile(videoFile))
        }
    }

    /**
     * copy video
     */
    private fun copyVideoToGallery(uriSavedVideo: Uri) {
        val resolver: ContentResolver = getApplication<Application>().contentResolver

        resolver.openFileDescriptor(uriSavedVideo, "w")?.let { parcelFile ->
            videoRecordedPath?.let { edited_file_path ->
                val videoFile = File(edited_file_path)
                val inputStream = FileInputStream(videoFile)
                val fileOutputStream = FileOutputStream(parcelFile.fileDescriptor)
                inputStream.copyTo(fileOutputStream, BUFFER_SIZE)
                inputStream.close()
                fileOutputStream.close()
                parcelFile.close()
                notifyFile()
            }
        }
    }

    /**
     * notify file to gallery
     */
    private fun notifyFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            videoUri?.let { uri ->
                contentValues?.let { values ->
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    getApplication<Application>().contentResolver.update(
                        uri, contentValues, null, null
                    )
                }
            }

        } else {
            videoPath?.let {
                MediaScannerConnection.scanFile(
                    getApplication(), arrayOf(it), null
                ) { path, uri ->
                    Log.e("XXX", "Scanned $path:")
                    Log.e("XXX", "-> uri=$uri")
                }
            }
        }
    }


}