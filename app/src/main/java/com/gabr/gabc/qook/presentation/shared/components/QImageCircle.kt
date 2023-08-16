package com.gabr.gabc.qook.presentation.shared.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun QImageCircle(
    uri: Uri,
    placeholder: ImageVector,
    size: Dp? = null,
    onClick: (() -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val sizeDefault = configuration.screenWidthDp.dp / 2f

    OutlinedButton(
        onClick = { onClick?.let { it() } },
        modifier = Modifier.size(size ?: sizeDefault),
        shape = CircleShape,
        enabled = onClick != null,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
        contentPadding = PaddingValues(0.dp),
    ) {
        if (uri == Uri.EMPTY) {
            Icon(
                placeholder,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.onBackground
            )
        } else {
            QImage(
                uri = uri,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}