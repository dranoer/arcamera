package com.trowical.arcamera.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * check permission/s and notify result
 */
fun ComponentActivity.checkPermissions(
    permissions: Array<String> = mainPermissions,
    actionResult: (permissionsGranted: Boolean) -> Unit
) {
    val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        result.entries.forEach { _ ->
            val granted = result.entries.all { entry -> entry.value }
            actionResult(granted)
        }
    }

    permissions.all { permission -> isGranted(permission, this) }
        .also { allGranted ->
            if (allGranted) {
                actionResult(true)
            } else {
                requestPermissions.launch(permissions)
            }
        }

}

/**
 * check if permission is granted
 */
private fun isGranted(
    permission: String, context: Context
): Boolean = ContextCompat.checkSelfPermission(
    context, permission
) == PackageManager.PERMISSION_GRANTED
