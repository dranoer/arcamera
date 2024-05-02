package com.trowical.arcamera.utils

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import com.trowical.arcamera.model.MediaItemObj
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * show toast
 */
fun Context.message(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * show dialog
 */
fun Context.showDeleteDialog(
    mediaItem: MediaItemObj,
    deleteClicked: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setTitle("Delete ${if (mediaItem.isVideo()) "video" else "image"}?")
        .setMessage(mediaItem.nameRaw)
        .setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
            deleteClicked()
        }
        .show()
}

/**
 * screen width
 */
fun Context.getScreenWidth() = resources.displayMetrics.widthPixels

/**
 * vibrate
 */
@Suppress("DEPRECATION")
fun Context.vibrate() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(20, 20))
    } else {
        (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(20)
    }
}