package com.iodroid.ar_nav.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun isLocationPermissionGranted(context: Context): Boolean {
    return (
        isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
            isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
}

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return (
        ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
        )
}
