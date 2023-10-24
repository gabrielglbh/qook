package com.gabr.gabc.qook.presentation.shared.components

import android.Manifest
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import com.gabr.gabc.qook.R

@Composable
fun QPhotoDialog(
    requestMultiplePermissions: ActivityResultLauncher<Array<String>>,
    focusManager: FocusManager? = null,
    onDismiss: () -> Unit
) {
    QDialog(
        onDismissRequest = { onDismiss() },
        leadingIcon = Icons.Outlined.Photo,
        title = R.string.photo_title,
        content = {
            Text(stringResource(R.string.photo_content))
        },
        buttonTitle = R.string.photo_gallery,
        onSubmit = {
            onDismiss()
            focusManager?.clearFocus()
            requestMultiplePermissions.launch(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                } else {
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            )
        },
        buttonSecondaryTitle = R.string.photo_camera,
        onSubmitSecondary = {
            onDismiss()
            focusManager?.clearFocus()
            requestMultiplePermissions.launch(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.CAMERA
                    )
                } else {
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                }
            )
        },
    )
}