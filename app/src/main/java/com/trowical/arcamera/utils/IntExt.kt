package com.trowical.arcamera.utils

import android.content.res.Resources

/**
 * convert int to pixels
 */
val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
