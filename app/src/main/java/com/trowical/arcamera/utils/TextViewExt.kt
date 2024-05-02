package com.trowical.arcamera.utils

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.core.view.isInvisible

/**
 * show-hide action effect
 */
fun TextView.showActionEffect(actionRes: Int?) {
    actionRes?.let { action ->
        text = context.getString(action)
        isInvisible = false
        Handler(Looper.getMainLooper()).postDelayed({
            isInvisible = true
        }, 1500)
    } ?: run {
        isInvisible = true
    }
}