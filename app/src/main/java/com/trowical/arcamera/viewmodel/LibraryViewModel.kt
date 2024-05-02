package com.trowical.arcamera.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.database.ContentObserver
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.trowical.arcamera.model.MediaItemObj
import com.trowical.arcamera.utils.CursorUtils
import com.trowical.arcamera.utils.registerMediaObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var contentObserver: ContentObserver? = null

    private val mediaItemsMLD = MutableLiveData<List<MediaItemObj>>()
    val mediaItems: LiveData<List<MediaItemObj>> = mediaItemsMLD

    private val firstMediaItemMLD = MutableLiveData<MediaItemObj>()
    val firstMediaItem: LiveData<MediaItemObj> = firstMediaItemMLD

    /**
     * load all photos and videos
     */
    fun loadMediaItems() {
        viewModelScope.launch {
            val items = getAllMediaItems()
            mediaItemsMLD.postValue(items)
            notifyFirstMediaItem(items)

            if (contentObserver == null) {
                contentObserver =
                    getApp().contentResolver.registerMediaObserver {
                        loadMediaItems()
                    }
            }
        }
    }

    /**
     * find and notify first media
     */
    private fun notifyFirstMediaItem(items: List<MediaItemObj>) {
        if (items.isNotEmpty()) {
            firstMediaItemMLD.postValue(items.first())
        }
    }

    /**
     * get app context
     */
    private fun getApp() = getApplication<Application>()

    /**
     * get photos and videos
     */
    @SuppressLint("InlinedApi")
    private suspend fun getAllMediaItems(): List<MediaItemObj> {

        val mediaItems = mutableListOf<MediaItemObj>()

        try {
            withContext(Dispatchers.IO) {
                getApp().contentResolver.query(
                    CursorUtils.CONTENT_URI,
                    CursorUtils.PROJECTION,
                    CursorUtils.SELECTION,
                    null,
                    CursorUtils.SORT_BY_DATE
                )?.use { cursor ->

                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val folderNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                    val mediaTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)
                    val dateAddedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val folderName = cursor.getString(folderNameColumn)
                        val nameRaw = cursor.getString(nameColumn)
                        val size = cursor.getLong(sizeColumn)
                        val mediaType = cursor.getInt(mediaTypeColumn)
                        val uri = ContentUris.withAppendedId(
                            CursorUtils.getMediaContentUri(mediaType), id
                        )
                        val mimeType = cursor.getString(mimeTypeColumn)
                        val duration = cursor.getLong(durationColumn)
                        val dateAdded = cursor.getLong(dateAddedColumn)

                        mediaItems += MediaItemObj(
                            id,
                            uri,
                            nameRaw,
                            folderName,
                            size,
                            mediaType,
                            mimeType,
                            duration,
                            dateAdded
                        )
                    }

                }
            }
        } catch (e: Exception) {
            e.message
        }
        return mediaItems
    }

    /**
     * Since we register a content observer, we want to unregister this when the viewModel
     * is being released.
     */
    override fun onCleared() {
        super.onCleared()
        contentObserver?.let {
            getApp().contentResolver.unregisterContentObserver(it)
        }
    }

}
