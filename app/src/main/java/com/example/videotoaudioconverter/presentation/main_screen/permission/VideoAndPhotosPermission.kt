package com.example.videotoaudioconverter.presentation.main_screen.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun VideoAndPhotoPermission(
    onGranted: () -> Unit,
    onDeniedTemporarily: () -> Unit,
    onDeniedPermanently: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val denied = result.entries.filter { !it.value }.map { it.key }

        when {
            denied.isEmpty() -> onGranted()
            denied.any { permission ->
                activity?.let {
                    !ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
                } == true
            } -> onDeniedPermanently()
            else -> onDeniedTemporarily()
        }
    }

    LaunchedEffect(Unit) {
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isEmpty()) {
            onGranted()
        } else {
            permissionLauncher.launch(notGranted.toTypedArray())
        }
    }
}
