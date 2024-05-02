package com.trowical.arcamera.viewmodel

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.IntentSender
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.trowical.arcamera.model.MediaItemObj
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteMediaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val deleteMediaMutableLiveData = MutableLiveData<IntentSender?>()
    val permissionForDeleteMedia: LiveData<IntentSender?> = deleteMediaMutableLiveData

    private val mediaDeletedMutableLiveData = MutableLiveData<Boolean>()
    val mediaDeleted: LiveData<Boolean> = mediaDeletedMutableLiveData

    //
    private var pendingDeleteMedia: MediaItemObj? = null

    /**
     * perform delete video
     */
    fun deleteMedia(mediaItem: MediaItemObj) {
        viewModelScope.launch {
            performDeleteMedia(mediaItem)
        }
    }

    /**
     * try again to delete video when user click on "Allow" button
     */
    fun deletePendingMedia() {
        pendingDeleteMedia?.let { media ->
            pendingDeleteMedia = null
            deleteMedia(media)
        }
    }

    /**
     * Delete video from gallery
     *
     * In [Build.VERSION_CODES.Q] and above, it isn't possible to modify
     * or delete items in MediaStore directly, and explicit permission
     * must usually be obtained to do this.
     *
     * The way it works is the OS will throw a [RecoverableSecurityException],
     * which we can catch here. Inside there's an [IntentSender] which the
     * activity can use to prompt the user to grant permission to the item
     * so it can be either updated or deleted.
     */
    private suspend fun performDeleteMedia(mediaItem: MediaItemObj) {
        withContext(Dispatchers.IO) {
            try {
                //numberRowsDeleted > 0 = files deleted
                mediaItem.uri?.let { uri ->
                    val numberRowsDeleted = getApplication<Application>().contentResolver.delete(
                        uri, null, null
                    )
                    mediaDeletedMutableLiveData.postValue(numberRowsDeleted > 0)
                }
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as? RecoverableSecurityException
                            ?: throw securityException

                    // Signal to the Activity that it needs to request permission and
                    // try the delete again if it succeeds.
                    pendingDeleteMedia = mediaItem
                    deleteMediaMutableLiveData.postValue(
                        recoverableSecurityException
                            .userAction.actionIntent.intentSender
                    )
                } else {
                    throw securityException
                }
            }
        }
    }

}