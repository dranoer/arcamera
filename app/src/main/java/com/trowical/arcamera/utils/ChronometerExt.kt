package com.trowical.arcamera.utils

import android.os.SystemClock
import android.widget.Chronometer
import androidx.core.view.isVisible

/**
 * show and start chronometer
 */
fun Chronometer.startChronometer() {
    isVisible = true
    base = SystemClock.elapsedRealtime()
    start()
}

/**
 * hide and stop chronometer
 */
fun Chronometer.stopChronometer() {
    isVisible = false
    stop()
}