package com.trowical.arcamera.utils

import android.content.Context
import android.text.format.Formatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * get formatted duration
 */
fun Long.getDuration(): String = SimpleDateFormat(
    "mm:ss",
    Locale.getDefault()
).format(Date(this))

/**
 * get file size
 */
fun Long.getSize(context: Context): String = Formatter.formatFileSize(context, this)