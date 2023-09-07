package com.gabr.gabc.qook.presentation.shared

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

class PermissionsRequester {
    companion object {
        fun requestMultiplePermissionsCaller(
            c: ComponentActivity,
            pickMedia: ActivityResultLauncher<PickVisualMediaRequest>,
        ): ActivityResultLauncher<Array<String>> {
            return c.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.forEach { actionMap ->
                    when (actionMap.key) {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                            if (!actionMap.value) {
                                c.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        }

                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            if (actionMap.value) {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } else {
                                c.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }

                        Manifest.permission.READ_MEDIA_IMAGES -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (actionMap.value) {
                                    pickMedia.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                } else {
                                    c.shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}